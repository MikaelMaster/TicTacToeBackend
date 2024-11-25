package com.mikael.tictactoebackend.db.schema.match

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Match(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Match>(MatchesTable)


}