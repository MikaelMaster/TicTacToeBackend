@file:Suppress("DUPLICATES")

package com.mikael.tictactoe.routing.user

import com.mikael.tictactoe.*
import com.mikael.tictactoe.db.schema.user.User
import com.mikael.tictactoe.db.schema.user.UsersTable
import com.mikael.tictactoe.plugin.generateJWTAccessToken
import com.mikael.tictactoe.plugin.jwtTokenCache
import com.mikael.tictactoe.routing.ErrorResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.or
import java.util.concurrent.TimeUnit

/**
 * Routing for [User] creation and authentication.
 */
internal fun Route.userRouting() {
    post("/check-availability") {
        val request = call.receive<UserCheckAvailabilityRequest>()

        val emailLowercase = request.email.lowercase()
        val foundUser = dbQuery {
            User.find {
                (UsersTable.nickname eq request.nickname) or
                        (UsersTable.email eq emailLowercase)
            }.limit(1).firstOrNull()
        }

        call.respond(
            HttpStatusCode.OK,
            UserCheckAvailabilityResponse(
                foundUser?.nickname == request.nickname,
                foundUser?.email == emailLowercase
            )
        )
    }

    post("/register") {
        val request = call.receive<UserRegisterRequest>()

        // Nickname validation
        if (!request.nickname.isTicTacToeNickname()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Nickname must contain only letters, numbers, and underscores.")
            )
            return@post
        }

        // Email validation
        val emailLowercase = request.email.lowercase()
        if (!emailLowercase.isEmail()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid email.")
            )
            return@post
        }

        // Password validation
        if (request.password.length !in 6..12) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Password must be between 6 and 12 characters.")
            )
            return@post
        }

        // Check if the user already exists with the same nickname or email
        val foundUser = dbQuery {
            User.find {
                (UsersTable.nickname eq request.nickname) or
                        (UsersTable.email eq emailLowercase)
            }.limit(1).firstOrNull()
        }
        if (foundUser != null) {
            if (foundUser.nickname == request.nickname && foundUser.email == emailLowercase) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Nickname and email already in use.")
                )
            }
            if (foundUser.nickname == request.nickname) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Nickname already in use.")
                )
            }
            if (foundUser.email == emailLowercase) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("Email already in use.")
                )
            }
            return@post
        }

        // Create the user
        val createdUser = dbQuery {
            User.new {
                nickname = request.nickname
                email = emailLowercase
                passwordHash = hashPassword(request.password)
            }
        }

        call.respond(HttpStatusCode.Created, UserRegisterResponse(UserResponse.fromUser(createdUser)))
    }

    post("/login") {
        val request = call.receive<UserLoginRequest>()

        val user = dbQuery {
            User.find {
                (UsersTable.nickname eq request.identifier) or
                        (UsersTable.email eq request.identifier.lowercase())
            }.limit(1).firstOrNull()
        }
        if (user == null) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("User not found for the given email/nickname.")
            )
            return@post
        }

        if (!checkPassword(request.password, user.passwordHash!!)) {
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse("Invalid password.")
            )
            return@post
        }

        // Generate the JWT access token and save it in the cache
        val generatedJWT =
            generateJWTAccessToken(user.id.value, (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
        jwtTokenCache.put(user.id.value, generatedJWT.tokenId)

        call.respond(UserLoginResponse(UserResponse.fromUser(user), generatedJWT.token))
    }

    authenticate("auth-jwt") {
        get("/me") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("User ID not found in the JWT token."))
                return@get
            }

            val user = dbQuery { User.findById(userId) }
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found."))
                return@get
            }

            call.respond(HttpStatusCode.OK, UserResponse.fromUser(user))
        }

        post("/logout") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("User ID not found in the JWT token."))
                return@post
            }

            val user = dbQuery { User.findById(userId) }
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found."))
                return@post
            }

            // Invalidate the JWT token, so the user will need to login again
            jwtTokenCache.invalidate(user.id.value)

            call.respond(HttpStatusCode.NoContent)
        }
    }
}