package com.mikael.tictactoebackend.routing.user

import com.mikael.tictactoebackend.ErrorResponse
import com.mikael.tictactoebackend.db.dbQuery
import com.mikael.tictactoebackend.db.schema.user.User
import com.mikael.tictactoebackend.db.schema.user.UsersTable
import com.mikael.tictactoebackend.isEmail
import com.mikael.tictactoebackend.isTicTacToeNickname
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.or

/**
 * Routes for [User] creation and authentication.
 */
internal fun Route.userRoutes() {
    post("/register") {
        val request = call.receive<UserRegisterRequest>()

        // Nickname validation
        if (!request.nickname.isTicTacToeNickname()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Nickname must contain only letters, numbers, and underscores.")
            )
        }

        // Email validation
        val email = request.email.lowercase()
        if (!email.isEmail()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid email.")
            )
        }

        // Password validation
        if (request.password.length !in 6..12) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Password must be between 6 and 12 characters.")
            )
        }

        // Check if the user already exists
        dbQuery {
            User.find {
                (UsersTable.nickname eq request.nickname) or
                        (UsersTable.email eq email)
            }.limit(1).firstOrNull()
        }?.let {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("An user with this nickname or email already exists.")
            )
        }
    }

    post("/login") {

    }

    authenticate("auth-jwt") {
        get("/me") {

        }

        post("/logout") {

        }
    }
}