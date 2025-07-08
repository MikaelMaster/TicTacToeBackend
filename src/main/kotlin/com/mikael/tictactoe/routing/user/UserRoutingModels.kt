package com.mikael.tictactoe.routing.user

import com.mikael.tictactoe.db.schema.user.User
import com.mikael.tictactoe.routing.Response
import kotlinx.serialization.Serializable

// Request models - Start
@Serializable
data class UserCheckAvailabilityRequest(
    val nickname: String,
    val email: String
)

@Serializable
data class UserRegisterRequest(
    val nickname: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val identifier: String, // Can be either a nickname or an email
    val password: String
)
// Request models - End

// Response models - Start
@Serializable
data class UserCheckAvailabilityResponse(
    val nicknameAlreadyInUse: Boolean,
    val emailAlreadyInUse: Boolean
) : Response(true)

@Serializable
data class UserResponse(
    val id: Long,
    val nickname: String,
    val email: String?
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