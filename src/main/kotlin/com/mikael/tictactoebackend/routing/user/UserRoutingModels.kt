package com.mikael.tictactoebackend.routing.user

import com.mikael.tictactoebackend.Response
import com.mikael.tictactoebackend.db.schema.user.User
import kotlinx.serialization.Serializable

// Request models - Start
@Serializable
data class UserRegisterRequest(
    val nickname: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val identifier: String, // Can be the nickname or the email
    val password: String
)
// Request models - End

// Response models - Start
@Serializable
data class UserResponse(
    val id: Long,
    val nickname: String,
    val email: String
) {
    companion object {
        /**
         * Returns a [UserResponse] from a [User].
         */
        fun fromUser(user: User): UserResponse {
            return UserResponse(
                user.id.value,
                user.nickname,
                user.email
            )
        }
    }
}

@Serializable
data class UserRegisterResponse(
    val user: UserResponse
) : Response(true)

@Serializable
data class UserLoginResponse(
    val user: UserResponse,
    val token: String
) : Response(true)
// Response models - End