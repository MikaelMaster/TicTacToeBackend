package com.mikael.tictactoe.routing.match

import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import java.util.*

// General models - Start
/**
 * Player model.
 *
 * @property id Player ID.
 * @property nick Player nickname.
 */
@Serializable
data class Player(
    val id: String = UUID.randomUUID().toString(),
    val nick: String,
    var session: DefaultWebSocketSession? = null
)

/**
 * Game session model.
 *
 * @property id Game session ID.
 * @property playerO Player O.
 * @property playerX Player X.
 * @property board Game board.
 * @property currentTurn Current turn.
 */
@Serializable
@Suppress("ArrayInDataClass")
data class GameSession(
    val id: String = UUID.randomUUID().toString(),
    val playerO: Player,
    val playerX: Player,
    var board: Array<Int?> = Array(9) { null },
    var currentTurn: Int = 0,
) {
    val currentPlayer get() = if (currentTurn == 0) playerO else playerX

    /**
     * Gets the player by the session.
     *
     * @param session The session.
     * @return The player that has the provided session.
     */
    fun getPlayerBySession(session: DefaultWebSocketSession): Player {
        return if (playerO.session == session) playerO else playerX
    }
}
// General models - End

// Request models - Start
/**
 * Queue join request model.
 *
 * @property nick Player nickname.
 */
@Serializable
data class QueueJoinRequest(val nick: String)

/**
 * Queue leave request model.
 *
 * @property playerId Player ID.
 */
@Serializable
data class QueueLeaveRequest(val playerId: String)
// Request models - End

// Response models - Start
/*
* @Serializable
data class MatchResults(
    val player1: UserResponse,
    val player2: UserResponse,
    val winner: UserResponse?,
    val isDraw: Boolean,
    val createdAt: LocalDateTime,
    val finishedAt: LocalDateTime?
) {
    companion object {
        /**
         * Returns a [MatchResults] from a [Match].
         */
        fun fromMatch(match: Match): MatchResults {
            return MatchResults(
                UserResponse.fromUser(match.player1),
                UserResponse.fromUser(match.player2),
                match.winner?.let { UserResponse.fromUser(it) },
                match.isDraw,
                match.createdAt,
                match.finishedAt
            )
        }
    }
}

@Serializable
data class MatchResultsResponse(val results: MatchResults) : Response(true)

/**
 * Queue join response model.
 *
 * @property player Player.
 * @see Response
 */
@Serializable
data class QueueJoinResponse(val player: Player) : Response(true)

/**
 * Game status response model.
 *
 * @property game Game session.
 * @see Response
 */
@Serializable
data class GameStatusResponse(val game: GameSession) : Response(true)
* */
// Response models - End