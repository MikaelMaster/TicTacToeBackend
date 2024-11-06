package com.mikael.tictactoebackend.routing.match

import com.mikael.tictactoebackend.ErrorResponse
import com.mikael.tictactoebackend.core.TicTacToeGame
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Defines the routing for the match endpoint.
 */
@Suppress("Duplicates")
internal fun Route.matchRouting() {
    // Defines the queue endpoint.
    route("/queue") {
        post("/join") {
            val request = call.receive<QueueJoinRequest>()

            if (request.nick.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Nick is required."))
                return@post
            }
            if (request.nick.length !in 3..16) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Nick must be between 3 and 16 characters."))
                return@post
            }

            // Adds the player to the game waiting queue.
            val player = Player(nick = request.nick)
            TicTacToeGame.waitingQueue.add(player)

            if (TicTacToeGame.waitingQueue.size < 2) {
                // If there is no other player in the queue, the player is added to the queue
                // and an OK response is sent back. The player will be waiting for another player to join.
                call.respond(HttpStatusCode.Accepted, QueueJoinResponse(player))
            }

            // If there are two players in the queue, a new game session will be created to start the game.
            val player1 = TicTacToeGame.waitingQueue.removeAt(0)
            val player2 = TicTacToeGame.waitingQueue.removeAt(0)
            val (playerO, playerX) = if (Math.random() > 0.5) player1 to player2 else player2 to player1

            // Creates a new game session.
            val gameSession = GameSession(playerO = playerO, playerX = playerX)
            TicTacToeGame.gameSessions.add(gameSession)

            call.respond(HttpStatusCode.OK, QueueJoinResponse(player))
        }

        post("/leave") {
            val request = call.receive<QueueLeaveRequest>()

            // Removes the player from the game waiting queue.
            TicTacToeGame.waitingQueue.removeIf { it.id == request.playerId }

            call.respond(HttpStatusCode.OK)
        }
    }

    // Defines the game endpoint.
    route("/game") {
        get("/status/{gameId}") {
            val gameId = call.parameters["gameId"]
            if (gameId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Game ID parameter is required."))
                return@get
            }

            // Finds the game session by the provided ID.
            val gameSession = TicTacToeGame.gameSessions.find { it.id == gameId }
            if (gameSession == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Game not found for the provided ID."))
                return@get
            }

            call.respond(HttpStatusCode.OK, GameStatusResponse(gameSession))
        }
    }
}