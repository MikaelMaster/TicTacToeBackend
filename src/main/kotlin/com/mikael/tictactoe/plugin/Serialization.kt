package com.mikael.tictactoe.plugin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * Configures the serialization settings for the application.
 */
internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            // prettyPrint = true
            isLenient = true
        })
    }
}