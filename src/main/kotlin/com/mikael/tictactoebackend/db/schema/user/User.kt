package com.mikael.tictactoebackend.db.schema.user

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Represents a user in the database.
 *
 * @property id The ID of the user.
 * @see UsersTable
 */
class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(UsersTable)

    var nickname by UsersTable.nickname

    var email by UsersTable.email
    var passwordHash by UsersTable.passwordHash

    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

}