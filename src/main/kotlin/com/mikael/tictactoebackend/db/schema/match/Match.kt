package com.mikael.tictactoebackend.db.schema.match

import com.mikael.tictactoebackend.db.schema.user.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Represents a match in the database.
 *
 * @property id The ID of the match.
 * @see MatchesTable
 */
class Match(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Match>(MatchesTable)

    var player1 by MatchesTable.player1
    var player2 by MatchesTable.player2

    var winner by User optionalReferencedOn MatchesTable.winner

    var createdAt by MatchesTable.createdAt
    var finishedAt by MatchesTable.finishedAt

}