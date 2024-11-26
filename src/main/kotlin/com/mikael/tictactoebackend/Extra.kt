package com.mikael.tictactoebackend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.toKotlinLocalDateTime
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime
import java.util.*

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

// JWT Token - Start
internal const val JWT_TOKEN_AUDIENCE = "tic-tac-toe-game"
internal const val JWT_TOKEN_ISSUER = "tic-tac-toe-backend"
internal val JWT_TOKEN_SECRET = serverDotEnv["JWT_TOKEN_SECRET"]!!

/**
 * Data class for storing the generated [JWT] access token and its id.
 */
internal data class GeneratedJWT(val token: String, val tokenId: String)

/**
 * Generates a [JWT] access token with the given parameters.
 *
 * @param userId the user id to be included in the [JWT] access token.
 * @param expiresAt the expiration time of the [JWT] access token in milliseconds since epoch.
 */
internal fun generateJWTAccessToken(userId: Long, expiresAt: Long): GeneratedJWT {
    val tokenId = UUID.randomUUID().toString()
    val token = JWT.create()
        .withAudience(JWT_TOKEN_AUDIENCE)
        .withIssuer(JWT_TOKEN_ISSUER)
        .withJWTId(tokenId)
        .withClaim("userId", userId)
        .withExpiresAt(Date(expiresAt))
        .sign(Algorithm.HMAC256(JWT_TOKEN_SECRET))
    return GeneratedJWT(token, tokenId)
}
// JWT Token - End