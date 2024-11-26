package com.mikael.tictactoebackend

import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

/**
 * Get the current time in Kotlin's [LocalDateTime] format
 */
internal fun nowKotlinDateTime() = LocalDateTime.now().toKotlinLocalDateTime()

/**
 * Check if a string is an email.
 */
internal fun String.isEmail(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$"))
}

/**
 * Check if a string is a game nickname.
 */
internal fun String.isTicTacToeNickname(): Boolean {
    return this.matches(Regex("^(?!_)[a-zA-Z0-9_]{3,16}(?<!_)$"))
}