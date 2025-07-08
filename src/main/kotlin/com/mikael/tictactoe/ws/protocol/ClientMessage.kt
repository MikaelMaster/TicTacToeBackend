package com.mikael.tictactoe.ws.protocol

import com.mikael.tictactoe.enums.MatchType
import kotlinx.serialization.SerialName

sealed class ClientMessage {

    // Matchmaking - Start
    @SerialName("matchmaking:find")
    data class FindMatch(val mode: MatchType) : ClientMessage()

    @SerialName("matchmaking:cancel")
    object CancelMatch : ClientMessage()
    // Matchmaking - End

    // Ready - Start
    @SerialName("ready")
    object Ready : ClientMessage()
    // Ready - End

    // Move - Start
    @SerialName("move")
    data class Move(
        val x: Int,
        val y: Int
    ) : ClientMessage()
    // Move - End

    // Surrender - Start
    @SerialName("surrender")
    object Surrender : ClientMessage()
    // Surrender - End

    // Stats - Start
    @SerialName("stats:get")
    object StatsGet : ClientMessage()
    // Stats - End

    // Ping - Start
    @SerialName("ping")
    object Ping : ClientMessage()
    // Ping - End

}