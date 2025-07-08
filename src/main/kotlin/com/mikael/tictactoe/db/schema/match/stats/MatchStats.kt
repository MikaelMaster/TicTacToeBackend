package com.mikael.tictactoe.db.schema.match.stats

import com.mikael.tictactoe.db.schema.match.Match
import com.mikael.tictactoe.db.schema.user.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Represents a match stats in the database.
 *
 * @property id the ID of the match stats.
 * @see MatchesStatsTable
 */
class MatchStats(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MatchStats>(MatchesStatsTable)

    var user by User referencedOn MatchesStatsTable.user
    var match by Match referencedOn MatchesStatsTable.match

    var result by MatchesStatsTable.result

    var movesCount by MatchesStatsTable.movesCount
    var timeSpent by MatchesStatsTable.timeSpent

    var createdAt by MatchesStatsTable.createdAt

}