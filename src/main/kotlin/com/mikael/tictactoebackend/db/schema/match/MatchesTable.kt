package com.mikael.tictactoebackend.db.schema.match

import com.mikael.tictactoebackend.db.schema.user.UsersTable
import com.mikael.tictactoebackend.enums.MatchType
import com.mikael.tictactoebackend.nowKotlinDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Represents the 'tictactoe_matches' table in the database.
 *
 * @see Match
 */
object MatchesTable : LongIdTable("tictactoe_matches") {

    var type = enumerationByName<MatchType>("type", 10)

    var player1 = reference("player1", UsersTable)
    var player2 = reference("player2", UsersTable)

    var winner = reference("winner", UsersTable).nullable()
    var isDraw = bool("is_draw").default(false)

    var createdAt = datetime("created_at").clientDefault { nowKotlinDateTime() }
    var finishedAt = datetime("finished_at").nullable()

}