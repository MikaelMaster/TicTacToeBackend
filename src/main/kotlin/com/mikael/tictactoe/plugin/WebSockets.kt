package com.mikael.tictactoe.plugin

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds

/**
 * Configures the WebSocket settings for the application.
 */
internal fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = true // This is technically not needed for a TicTacToe game, but... Don't question it.
    }
}