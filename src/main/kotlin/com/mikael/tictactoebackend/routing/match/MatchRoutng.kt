@file:Suppress("DUPLICATES")

package com.mikael.tictactoebackend.routing.match

import com.mikael.tictactoebackend.ErrorResponse
import com.mikael.tictactoebackend.core.TicTacToeGame
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

/**
 * Routing for the match endpoints.
 */
internal fun Route.matchRouting() {
    webSocket("/ws/{gameId}") {
        val gameId = call.parameters["gameId"]
        if (gameId == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Game ID parameter is required."))
            return@webSocket
        }

        val gameSession = TicTacToeGame.gameSessions.find { it.id == gameId }
        if (gameSession == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Game session not found."))
            return@webSocket
        }

        if (gameSession.playerO.session == null) {
            gameSession.playerO.apply { session = this@webSocket }
        } else {
            gameSession.playerX.apply { session = this@webSocket }
        }

        // Starts the game and sends the game update to the players.
        sendGameUpdate(gameSession)

        // Handles the incoming messages.
        for (input in incoming) {
            try {
                when (input) {
                    is Frame.Text -> {
                        val move = input.readText().toInt()
                        if (TicTacToeGame.makeMove(gameSession, move)) {
                            sendGameUpdate(gameSession)

                            val result = TicTacToeGame.endGame(gameSession)
                            if (result != null) {
                                sendGameOver(gameSession, result)
                                break
                            }
                        } else {
                            send(Frame.Text("Invalid move. Please, try again."))
                        }
                    }

                    is Frame.Close -> {
                        val player = gameSession.getPlayerBySession(this)
                        player.session = null
                        send(Frame.Close())

                        // If the player leaves the game, the other player wins.
                        val winner = if (player == gameSession.playerO) gameSession.playerX else gameSession.playerO
                        sendGameOver(gameSession, "Your opponent has left the game. You win, ${winner.nick}!")
                        break
                    }

                    else -> {} // Ignore other frames. Maybe any other frame type can be added here in the future.
                }
            } catch (ex: Exception) {
                send(Frame.Text("Error while processing your move: ${ex.message}"))
            }
        }
    }

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
}

/**
 * Sends the game update to the players.
 *
 * @param gameSession The game session.
 */
private suspend fun DefaultWebSocketSession.sendGameUpdate(gameSession: GameSession) {
    val board = gameSession.board.joinToString(",") {
        when (it) {
            0 -> "O"
            1 -> "X"
            else -> " " // null
        }
    }
    val currentPlayerNick = gameSession.currentPlayer.nick
    send(Frame.Text("Game update: $board, $currentPlayerNick"))
}

/**
 * Sends the game over message to the players.
 *
 * @param gameSession The game session.
 * @param result The result of the game as a [String].
 */
private suspend fun DefaultWebSocketSession.sendGameOver(gameSession: GameSession, result: String) {
    send(Frame.Text("Game over: $result"))
    gameSession.playerO.session?.close()
    gameSession.playerX.session?.close()
}