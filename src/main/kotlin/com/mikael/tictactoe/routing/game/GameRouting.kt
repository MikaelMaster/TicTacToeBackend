package com.mikael.tictactoe.routing.game

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mikael.tictactoe.db.schema.user.User
import com.mikael.tictactoe.dbQuery
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

// Cache for storing game websocket sessions.
private val gameWebSocketSessions: Cache<Long, DefaultWebSocketSession> =
    Caffeine.newBuilder().build()!! // UserID -> DefaultWebSocketSession

/**
 * Routing for the game endpoints.
 */
internal fun Route.gameRouting() {
    authenticate("auth-jwt") {
        webSocket("/ws") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
            if (userId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User ID not found in JWT."))
                return@webSocket
            }

            val user = dbQuery { User.findById(userId) }
            if (user == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not found."))
                return@webSocket
            }

            // Continuar aqui enviando para o cliente o 'WSGameConnectResponse'.
        }
    }
}