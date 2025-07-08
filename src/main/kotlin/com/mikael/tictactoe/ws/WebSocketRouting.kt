package com.mikael.tictactoe.ws

import com.mikael.tictactoe.db.schema.user.User
import com.mikael.tictactoe.receiveAs
import com.mikael.tictactoe.sendJson
import com.mikael.tictactoe.ws.protocol.ClientMessage
import com.mikael.tictactoe.ws.protocol.ServerMessage
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

/**
 * WebSocket routing for the application.
 */
internal fun Route.wsRouting() {
    webSocket {
        // Auth - Start
        val token = call.request.queryParameters["token"]
        val user = authenticateUserFromToken(token) // TODO: change this to a real JWT or database lookup
        if (user == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
            return@webSocket
        }

        sendJson(ServerMessage.AuthSuccess(user.id.value, user.nickname))
        // Auth - End

        // Listen for incoming messages and handle them
        try {
            while (true) {
                val fromClient = incoming.receiveAs<ClientMessage>()
                handleClientMessage(fromClient, user)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            // TODO: log error and handle disconnection.
        }
    }
}

private fun authenticateUserFromToken(token: String?): User? {
    // TODO: change this to a real JWT or database lookup
    if (token == null || token.length < 3) return null
    return User.findById(token.toLongOrNull() ?: return null)
}