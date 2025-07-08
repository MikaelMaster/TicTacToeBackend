package com.mikael.tictactoe.db.schema.user

import com.mikael.tictactoe.db.schema.match.stats.MatchStats
import com.mikael.tictactoe.db.schema.match.stats.MatchesStatsTable
import com.mikael.tictactoe.enums.MatchResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder

/**
 * Represents a user in the database.
 *
 * @property id the ID of the user.
 * @see UsersTable
 */
class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(UsersTable)

    var nickname by UsersTable.nickname
    var isGuest by UsersTable.isGuest

    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash

    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

    // Getters for match stats related to this user - Start
    /**
     * Returns a list of match statistics for this user, ordered by creation date in descending order (newest first).
     *
     * For overall statistics, use [stats].
     *
     * Requires to be run in a [transaction] context.
     *
     * @return list of [MatchStats] associated with this user.
     */
    val matchStats: List<MatchStats>
        get() {
            return MatchStats
                .find { MatchesStatsTable.user eq this@User.id }
                .orderBy(MatchesStatsTable.createdAt to SortOrder.DESC)
                .toList()
        }

    /**
     * Represents the statistics of a user in terms of match performance.
     */
    @Serializable
    data class Stats(
        val matchesPlayed: Int,
        val wins: Int,
        val losses: Int,
        val ties: Int,
        val totalMoves: Int,
        val totalTimeSpent: Int, // seconds
        val winRate: Double // 0.0 to 1.0, where 1.0 is 100% win rate
    )

    /**
     * Returns the overall statistics of this user based on their match stats.
     * This includes the number of matches played, wins, losses, ties,
     * total moves made, and total time spent in seconds.
     *
     * For detailed match statistics, use [matchStats].
     *
     * Requires to be run in a [transaction] context.
     *
     * @return [Stats] object containing the overall statistics of the user.
     */
    val stats: Stats
        get() {
            val list = matchStats
            val wins = list.count { it.result == MatchResult.WIN }
            val losses = list.count { it.result == MatchResult.LOSS }
            val ties = list.count { it.result == MatchResult.TIE }
            val played = list.size
            val moves = list.sumOf { it.movesCount }
            val time = list.sumOf { it.timeSpent }
            val rate = if (played > 0) wins.toDouble() / played else 0.0

            return Stats(played, wins, losses, ties, moves, time, rate)
        }
    // Getters for match stats related to this user - End

}