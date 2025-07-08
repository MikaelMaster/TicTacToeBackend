package com.mikael.tictactoe

import com.mikael.tictactoe.ws.protocol.ServerMessage
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

/**
 * This function is used to run a database query in a coroutine scope.
 *
 * @param block operation to be executed in the transaction context.
 * @see newSuspendedTransaction
 */
internal suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

/**
 * This function is used to receive a message from a [DefaultWebSocketSession] and decode it to the specified type.
 *
 * @return the received message in the specified type [T].
 * @throws IllegalArgumentException if the received frame is not a text frame.
 * @throws SerializationException if the frame cannot be deserialized to the specified type.
 */
internal suspend inline fun <reified T> ReceiveChannel<Frame>.receiveAs(): T {
    val frame = this.receive() as? Frame.Text
        ?: throw IllegalArgumentException("Expected a text frame but received a different type.")

    return Json.decodeFromString<T>(frame.readText())
}

/**
 * This function is used to send a [ServerMessage] as a JSON string to the [DefaultWebSocketServerSession].
 *
 * @param message the [ServerMessage] to be sent.
 * @throws SerializationException if the message cannot be serialized to JSON.
 */
suspend fun DefaultWebSocketServerSession.sendJson(message: ServerMessage) {
    outgoing.send(Frame.Text(Json.encodeToString(message)))
}

/**
 * Get the current time in Kotlin's [LocalDateTime] format
 */
internal fun nowKotlinDateTime() = LocalDateTime.now().toKotlinLocalDateTime()

/**
 * Check if a string is an email.
 */
internal fun String.isEmail(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
}

/**
 * Check if a string is a game nickname.
 */
internal fun String.isTicTacToeNickname(): Boolean {
    return this.matches(Regex("^(?!_)[a-zA-Z0-9_]{3,16}(?<!_)$"))
}

// Password hashing - Start
/**
 * @return a [String] with the hashed password.
 */
internal fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

/**
 * @return a [Boolean] indicating if the password is correct.
 */
internal fun checkPassword(password: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(password, hashedPassword)
}
// Password hashing - End