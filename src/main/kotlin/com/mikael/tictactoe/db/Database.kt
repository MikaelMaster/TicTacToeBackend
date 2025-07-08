package com.mikael.tictactoe.db

import com.mikael.tictactoe.db.schema.match.MatchesTable
import com.mikael.tictactoe.db.schema.match.stats.MatchesStatsTable
import com.mikael.tictactoe.db.schema.user.UsersTable
import com.mikael.tictactoe.dbQuery
import com.mikael.tictactoe.dotenv
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import kotlin.time.Duration.Companion.minutes

// Database that is used by Exposed.
private lateinit var exposedDB: Database

// Holds the jdbcUrl and driverClassName for the database connection.
// This is used to configure the database Hikari connection pool.
private data class DBSimpleConfig(val jdbcUrl: String, val driverClassName: String)

/**
 * Prepares the database by configuring the connection.
 */
internal fun Application.configureDatabase() {
    val engine = dotenv["DB_ENGINE"]
    val host = dotenv["DB_HOST"]
    val port = dotenv["DB_PORT"]!!.toInt()
    val name = dotenv["DB_NAME"]
    val user = dotenv["DB_USER"]
    val pass = dotenv["DB_PASSWORD"]
    val poolMinSize = dotenv["DB_POOL_MIN_SIZE"]!!.toInt()
    val poolMaxSize = dotenv["DB_POOL_MAX_SIZE"]!!.toInt()

    val simpleConfig = when (engine.lowercase()) {
        "mysql" -> DBSimpleConfig(
            "jdbc:mysql://${host}:${port}/${name}",
            "com.mysql.cj.jdbc.Driver"
        )

        "mariadb" -> DBSimpleConfig(
            "jdbc:mariadb://${host}:${port}/${name}",
            "org.mariadb.jdbc.Driver"
        )

        else -> throw IllegalArgumentException("Invalid database engine '${engine}'. Valid values are: mysql, mariadb.")
    }

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = simpleConfig.jdbcUrl
        username = user
        password = pass
        driverClassName = simpleConfig.driverClassName

        maximumPoolSize = poolMaxSize
        minimumIdle = poolMinSize
        idleTimeout = 10L // 10 milliseconds
        maxLifetime = 30.minutes.inWholeMilliseconds
    }

    exposedDB = Database.connect(HikariDataSource(hikariConfig))
}

/**
 * Configures the database schema by creating the necessary tables.
 */
@Suppress("DEPRECATION") // SchemaUtils#createMissingTablesAndColumns
internal suspend fun Application.configureSchema() {
    dbQuery {
        // In production, 'create' method should be used instead of 'createMissingTablesAndColumns'.
        SchemaUtils.createMissingTablesAndColumns(
            UsersTable,
            MatchesTable,
            MatchesStatsTable
        )
    }
}