package com.mikael.tictactoebackend.db.schema.match.result

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MatchResults(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MatchResults>(MatchesResultsTable)


}