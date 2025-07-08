package com.mikael.tictactoe.routing.game

import kotlinx.serialization.Serializable

@Serializable
enum class WSAction {
    GAME_CONNECT,
    SEND_INVITE,
    ACCEPT_INVITE
}

@Serializable
open class WSRequest(
    val action: WSAction
)

// Websocket request models - Start
@Serializable
data class WSSendInviteRequest(val targetNickname: String) : WSRequest(WSAction.SEND_INVITE)

@Serializable
data class WSAcceptInviteRequest(val inviteId: Long) : WSRequest(WSAction.ACCEPT_INVITE)
// Websocket request models - End

@Serializable
enum class WSStatus {
    CONNECTED,
    DISCONNECTED,
    ERROR,
    PENDING
}

@Serializable
open class WSResponse(
    val status: WSStatus
)

@Serializable
open class WSErrorResponse(
    val error: String,
    val errorCode: Int? = null
) : WSResponse(WSStatus.ERROR)

// Websocket response models - Start
@Serializable
data class WSGameConnectResponse(val sessionId: Long) : WSResponse(WSStatus.CONNECTED)

@Serializable
data class WSSendInviteResponse(val inviteId: Long) : WSResponse(WSStatus.PENDING)

@Serializable
data class WSAcceptInviteResponse(val matchId: Long) : WSResponse(WSStatus.CONNECTED)
// Websocket response models - End