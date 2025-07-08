package com.mikael.tictactoe.ws

import com.mikael.tictactoe.db.schema.user.User
import com.mikael.tictactoe.dbQuery
import com.mikael.tictactoe.sendJson
import com.mikael.tictactoe.ws.protocol.ClientMessage
import com.mikael.tictactoe.ws.protocol.ServerMessage
import io.ktor.server.websocket.*

/**
 * Handles incoming client messages in a WebSocket session.
 *
 * @param msg the client message received.
 * @param user the authenticated user who sent the message.
 */
internal suspend fun DefaultWebSocketServerSession.handleClientMessage(msg: ClientMessage, user: User) {
    when (msg) {
        is ClientMessage.FindMatch -> sendJson(ServerMessage.MatchmakingWaiting)

        is ClientMessage.CancelMatch -> sendJson(ServerMessage.MatchmakingCancelled)

        is ClientMessage.Ready -> {} // says that the user is ready to play

        is ClientMessage.Move -> {} // register the move

        is ClientMessage.Surrender -> {} // surrender the match (player gives up)

        is ClientMessage.StatsGet -> {
            val stats = dbQuery { user.stats }
            sendJson(ServerMessage.StatsResponse(stats))
        }

        is ClientMessage.Ping -> sendJson(ServerMessage.Pong)
    }
}