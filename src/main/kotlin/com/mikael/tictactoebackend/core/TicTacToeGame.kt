package com.mikael.tictactoebackend.core

import com.mikael.tictactoebackend.routing.match.GameSession
import com.mikael.tictactoebackend.routing.match.Player
import java.util.*

/**
 * The Tic-Tac-Toe game object.
 *
 * This object contains the game state and the game logic.
 *
 * @see Player
 */
internal object TicTacToeGame {

    internal val waitingQueue = Collections.synchronizedList(mutableListOf<Player>()) // Players waiting for a match.
    internal val gameSessions = Collections.synchronizedList(mutableListOf<GameSession>()) // Game sessions happening.

    /**
     * Verifies if the move is valid.
     *
     * @param gameSession The game session.
     * @param position The position to be checked.
     *
     * @return True if the move is valid, false otherwise.
     */
    private fun isValidMove(gameSession: GameSession, position: Int): Boolean {
        // Verifies if the position is valid.
        if (position !in 0..8) return false

        // Verifies if the position is empty.
        if (gameSession.board[position] != -1) return false

        return true
    }

    /**
     * Makes a move in the game.
     *
     * @param gameSession The game session.
     * @param position The position to make the move.
     *
     * @return True if the move was successful, false otherwise.
     */
    internal fun makeMove(gameSession: GameSession, position: Int): Boolean {
        // Verifies if the move is valid.
        if (!isValidMove(gameSession, position)) return false

        // Makes the move.
        gameSession.board[position] = gameSession.currentTurn

        // Changes the turn.
        gameSession.currentTurn = 1 - gameSession.currentTurn

        return true
    }

    /**
     * Checks if there is a winner.
     *
     * @param gameSession The game session.
     *
     * @return The winner player ID if there is a winner, *null* otherwise.
     */
    private fun checkWinner(gameSession: GameSession): Int? {
        // Verifies rows.
        for (i in 0..2) {
            if (gameSession.board[i * 3] == gameSession.board[i * 3 + 1] && gameSession.board[i * 3 + 1] == gameSession.board[i * 3 + 2] && gameSession.board[i * 3] != -1) {
                return gameSession.board[i * 3]
            }
        }

        // Verifies columns.
        for (i in 0..2) {
            if (gameSession.board[i] == gameSession.board[i + 3] && gameSession.board[i + 3] == gameSession.board[i + 6] && gameSession.board[i] != -1) {
                return gameSession.board[i]
            }
        }

        // Verifies diagonals.
        if (gameSession.board[0] == gameSession.board[4] && gameSession.board[4] == gameSession.board[8] && gameSession.board[0] != -1) {
            return gameSession.board[0]
        }
        if (gameSession.board[2] == gameSession.board[4] && gameSession.board[4] == gameSession.board[6] && gameSession.board[2] != -1) {
            return gameSession.board[2]
        }

        // If there is no winner.
        return null
    }

    /**
     * Checks if the game is a draw.
     *
     * @param gameSession The game session.
     * @return True if the game is a draw, false otherwise.
     */
    private fun checkDraw(gameSession: GameSession): Boolean {
        return gameSession.board.none { it == -1 } && checkWinner(gameSession) == -1
    }

    /**
     * Ends the game.
     *
     * @param gameSession The game session.
     *
     * @return The game result as a 'tile' ([String]) to be displayed.
     */
    internal fun endGame(gameSession: GameSession): String? {
        checkWinner(gameSession)?.let { winner ->
            return if (winner == 0) "${gameSession.playerO.nick} wins!" else "${gameSession.playerX.nick} wins!"
        }

        return if (checkDraw(gameSession)) "It's a draw!" else null
    }

    /**
     * Resets the board.
     *
     * @param gameSession The game session.
     */
    internal fun resetBoard(gameSession: GameSession) {
        gameSession.board.fill(-1) // Resets the board.
        gameSession.currentTurn = 0 // Resets the turn.
    }

}