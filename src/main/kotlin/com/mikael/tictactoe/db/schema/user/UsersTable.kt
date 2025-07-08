package com.mikael.tictactoe.db.schema.user

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Represents the 'tictactoe_users' table in the database.
 *
 * @see User
 */
object UsersTable : LongIdTable("tictactoe_users") {

    val nickname = varchar("nickname", 16).uniqueIndex()
    val isGuest = bool("is_guest").default(true)

    val email = varchar("email", 255).nullable().uniqueIndex()
    val passwordHash = varchar("password_hash", 60).nullable()

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

}