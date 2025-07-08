package com.mikael.tictactoe.ws.protocol

import com.mikael.tictactoe.db.schema.user.User
import kotlinx.serialization.SerialName

/**
 *
 */
sealed class ServerMessage {

    // Auth - Start
    @SerialName("auth:success")
    data class AuthSuccess(
        val userId: Long,
        val nickname: String
    ) : ServerMessage()
    // Auth - End

    // Matchmaking - Start
    @SerialName("matchmaking:waiting")
    object MatchmakingWaiting : ServerMessage()

    @SerialName("matchmaking:found")
    data class MatchmakingFound(val matchId: String) : ServerMessage()

    @SerialName("matchmaking:cancelled")
    object MatchmakingCancelled : ServerMessage()
    // Matchmaking - End

    // Match - Start
    @SerialName("match:connected")
    data class MatchConnected(
        val youAre: Char,
        val opponent: String
    ) : ServerMessage()

    @SerialName("match:start")
    data class MatchStart(
        val boardSize: Int,
        val round: Int,
        val timePerMove: Int
    ) : ServerMessage()
    // Match - End

    // Turn - Start
    @SerialName("turn:your")
    object TurnYour : ServerMessage()

    @SerialName("turn:opponent")
    data class TurnOpponent(val player: Char) : ServerMessage()
    // Turn - End

    // Move - Start
    @SerialName("move:made")
    data class MoveMade(
        val x: Int,
        val y: Int,
        val by: Char
    ) : ServerMessage()
    // Move - End

    // Round - Start
    @SerialName("round:end")
    data class RoundEnd(val winner: Char?) : ServerMessage()
    // Round - End

    // MatchEnd - Start
    @SerialName("match:end")
    data class MatchEnd(
        val winner: Char?,
        val score: Map<Char, Int>
    ) : ServerMessage()
    // MatchEnd - End

    // Stats - Start
    @SerialName("stats:response")
    data class StatsResponse(val stats: User.Stats) : ServerMessage()
    // Stats - End

    // Ping - Start
    @SerialName("pong")
    object Pong : ServerMessage()
    // Ping - End

}