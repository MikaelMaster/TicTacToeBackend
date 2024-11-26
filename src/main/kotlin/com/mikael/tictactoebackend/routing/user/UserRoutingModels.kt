package com.mikael.tictactoebackend.routing.user

import com.mikael.tictactoebackend.Response
import kotlinx.serialization.Serializable

// Request models - Start
@Serializable
data class UserRegisterRequest(
    val nickname: String,
    val email: String,
    val password: String
)
// Request models - End

// Response models - Start
@Serializable
data class UserResponse(
    val id: Long,
    val nickname: String,
    val email: String
)

@Serializable
data class UserRegisterResponse(
    val user: UserResponse
) : Response(true)
// Response models - End