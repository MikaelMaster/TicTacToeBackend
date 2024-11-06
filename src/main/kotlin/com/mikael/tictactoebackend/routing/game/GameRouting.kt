package com.mikael.tictactoebackend.routing.game

import com.mikael.tictactoebackend.ErrorResponse
import com.mikael.tictactoebackend.core.TicTacToeGame
import com.mikael.tictactoebackend.routing.match.GameSession
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

/**
 * Defines the routing for the game endpoint.
 */
@Suppress("Duplicates")
internal fun Route.gameRouting() {
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