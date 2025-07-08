package com.mikael.tictactoe.db.schema.match.stats

import com.mikael.tictactoe.db.schema.match.MatchesTable
import com.mikael.tictactoe.db.schema.user.UsersTable
import com.mikael.tictactoe.enums.MatchResult
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Represents the 'tictactoe_matches_stats' table in the database.
 *
 * @see MatchStats
 */
object MatchesStatsTable : LongIdTable("tictactoe_matches_stats") {

    val user = reference("user_id", UsersTable)
    val match = reference("match_id", MatchesTable)

    val result = enumerationByName<MatchResult>("result", 4)

    val movesCount = integer("moves_count").default(0)
    val timeSpent = integer("time_spent").default(0)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex(user, match)
    }

}