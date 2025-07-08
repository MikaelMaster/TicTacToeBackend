package com.mikael.tictactoe.routing

import com.mikael.tictactoe.routing.match.matchRouting
import com.mikael.tictactoe.routing.user.userRouting
import com.mikael.tictactoe.ws.wsRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Creates the routes for the application.
 */
internal fun Application.doRouting() {
    routing {
        route("/user") {
            userRouting()
            invalidRoute()
        }

        route("/match") {
            matchRouting()
            invalidRoute()
        }

        route("/ws") {
            wsRouting()
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
            call.respond(HttpStatusCode.NotFound, ErrorResponse("invalid_route"))
        }
    }
}