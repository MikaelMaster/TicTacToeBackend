package com.mikael.tictactoebackend.db.schema.user

import com.mikael.tictactoebackend.nowKotlinDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Represents the 'tictactoe_users' table in the database.
 *
 * @see User
 */
object UsersTable : LongIdTable("tictactoe_users") {

    var nickname = varchar("nickname", 16).uniqueIndex()

    var email = varchar("email", 255).uniqueIndex()
    var passwordHash = varchar("password_hash", 60)

    var createdAt = datetime("created_at").clientDefault { nowKotlinDateTime() }
    var updatedAt = datetime("updated_at").nullable()

}