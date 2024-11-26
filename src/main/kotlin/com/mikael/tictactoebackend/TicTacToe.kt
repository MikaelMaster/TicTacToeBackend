package com.mikael.tictactoebackend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mikael.tictactoebackend.db.DatabaseUtils
import com.mikael.tictactoebackend.routing.match.matchRouting
import com.mikael.tictactoebackend.routing.user.userRouting
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

// Basic response models - Start
/**
 * Base response model. All response models extend this model.
 *
 * @property success Response success status.
 */
@Serializable
@Suppress("WARNINGS")
open class Response(val success: Boolean)

/**
 * Error response model. Used when a request fails.
 *
 * @property error Error message.
 * @see Response
 */
@Serializable
data class ErrorResponse(val error: String) : Response(false)
// Basic response models - End

// Load environment variables.
internal val serverDotEnv: Dotenv = dotenv()

// Cache for storing logged in users tokens.
val jwtTokenCache: Cache<Long, String> = Caffeine.newBuilder()
    .expireAfterWrite(30, TimeUnit.DAYS)
    .build()!! // UserID -> TokenID (UUID)

fun main() {
    embeddedServer(
        Netty,
        host = serverDotEnv["RUNNING_HOST"]!!,
        port = serverDotEnv["RUNNING_PORT"]!!.toInt(),
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = true // This is technically not needed for a TicTacToe game, but... Don't question it.
    }
    install(StatusPages) {
        exception<Throwable> { call, exception ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(exception.message ?: "An unexpected error occurred.")
            )
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "tic-tac-toe-game"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_TOKEN_SECRET))
                    .withAudience(JWT_TOKEN_AUDIENCE)
                    .withIssuer(JWT_TOKEN_ISSUER)
                    .withClaimPresence("userId")
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asLong()!!
                val tokenId = credential.jwtId!!
                if (jwtTokenCache.getIfPresent(userId) == tokenId) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid access token."))
            }
        }
    }

    // Database
    DatabaseUtils.prepareDB()

    // Routing
    routing {
        route("/user") {
            userRouting()
            invalidRoute()
        }

        route("/match") {
            matchRouting()
            invalidRoute()
        }

        invalidRoute()
    }
}

/**
 * Invalid route handler. Responds with a 404 status code and an error message.
 *
 * @see ErrorResponse
 */
private fun Route.invalidRoute() {
    route("{...}") {
        get {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Invalid route."))
        }
    }
}