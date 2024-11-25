package com.mikael.tictactoebackend.db

import com.mikael.tictactoebackend.db.schema.match.MatchesTable
import com.mikael.tictactoebackend.db.schema.match.result.MatchesResultsTable
import com.mikael.tictactoebackend.db.schema.user.UsersTable
import com.mikael.tictactoebackend.serverDotEnv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This function is used to run a database query in a coroutine scope.
 *
 * @param block The block of code to run in the coroutine scope.
 * @see newSuspendedTransaction
 */
internal suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

object DatabaseUtils {
    private lateinit var exposedDB: Database

    internal fun prepareDB() {
        val dbEngine = serverDotEnv.get("DB_ENGINE")
        val host = serverDotEnv.get("DB_HOST")
        val port = serverDotEnv.get("DB_PORT")
        val dbName = serverDotEnv.get("DB_NAME")
        val user = serverDotEnv.get("DB_USER")
        val pass = serverDotEnv.get("DB_PASSWORD")

        when (dbEngine.lowercase()) {
            "mysql" -> {
                exposedDB = Database.connect(
                    "jdbc:mysql://${host}:${port}/${dbName}",
                    "com.mysql.cj.jdbc.Driver",
                    user, pass
                )
            }

            "mariadb" -> {
                exposedDB = Database.connect(
                    "jdbc:mariadb://${host}:${port}/${dbName}",
                    "org.mariadb.jdbc.Driver",
                    user, pass
                )
            }

            else -> throw IllegalArgumentException("Invalid database engine '${dbEngine}' set in .env file. Valid values are: mysql, mariadb.")
        }

        transaction {
            SchemaUtils.create(
                UsersTable,
                MatchesTable,
                MatchesResultsTable
            )
        }
    }
}