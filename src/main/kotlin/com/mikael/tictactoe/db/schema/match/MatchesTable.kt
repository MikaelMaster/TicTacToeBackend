package com.mikael.tictactoe.db.schema.match

import com.mikael.tictactoe.db.schema.user.UsersTable
import com.mikael.tictactoe.enums.MatchType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Represents the 'tictactoe_matches' table in the database.
 *
 * @see Match
 */
object MatchesTable : UUIDTable("tictactoe_matches") {

    val displayCode = varchar("display_code", 8).uniqueIndex().nullable()

    val mode = enumerationByName<MatchType>("mode", 8)

    val playerX = reference("player_x_id", UsersTable).nullable()
    val playerO = reference("player_o_id", UsersTable).nullable()
    val winner = reference("winner_id", UsersTable).nullable()

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

}