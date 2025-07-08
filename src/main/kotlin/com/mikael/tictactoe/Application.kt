package com.mikael.tictactoe

import com.mikael.tictactoe.db.configureDatabase
import com.mikael.tictactoe.db.configureSchema
import com.mikael.tictactoe.plugin.configureSecurity
import com.mikael.tictactoe.plugin.configureSerialization
import com.mikael.tictactoe.plugin.configureWebSockets
import com.mikael.tictactoe.routing.doRouting
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// Load environment variables.
internal val dotenv = dotenv()

fun main() {
    embeddedServer(
        Netty,
        host = dotenv["SERVER_HOST"]!!,
        port = dotenv["SERVER_PORT"]!!.toInt(),
        module = Application::module
    ).start(wait = true)
}

suspend fun Application.module() {
    // Plugins
    configureSerialization()
    configureWebSockets()
    configureSecurity()

    // Database
    configureDatabase()
    configureSchema()

    // Routing
    doRouting()
}