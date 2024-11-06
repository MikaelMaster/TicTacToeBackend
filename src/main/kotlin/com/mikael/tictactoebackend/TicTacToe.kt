package com.mikael.tictactoebackend

import com.mikael.tictactoebackend.routing.game.gameRouting
import com.mikael.tictactoebackend.routing.match.matchRouting
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

fun main() {
    embeddedServer(
        Netty,
        port = 8081,
        host = "0.0.0.0",
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

    routing {
        route("/match") {
            matchRouting()
            invalidRoute()
        }

        route("/game") {
            gameRouting()
            invalidRoute()
        }

        invalidRoute()
    }
}

private fun Route.invalidRoute() {
    route("{...}") {
        get {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Invalid route."))
        }
    }
}