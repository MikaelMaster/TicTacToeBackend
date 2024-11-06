package com.mikael.tictactoebackend.routing.match

import com.mikael.tictactoebackend.Response
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
// Response models - End