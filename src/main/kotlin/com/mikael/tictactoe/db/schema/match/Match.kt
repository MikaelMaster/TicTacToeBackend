package com.mikael.tictactoe.db.schema.match

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/**
 * Represents a match in the database.
 *
 * @property id the ID of the match.
 * @see MatchesTable
 */
class Match(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Match>(MatchesTable)

    var displayCode by MatchesTable.displayCode

    var mode by MatchesTable.mode

    var playerX by MatchesTable.playerX
    var playerO by MatchesTable.playerO
    var winner by MatchesTable.winner

    var createdAt by MatchesTable.createdAt
    var updatedAt by MatchesTable.updatedAt

}