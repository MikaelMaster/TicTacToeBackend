package com.mikael.tictactoebackend.enums

/**
 * Represents the type of a tic-tac-toe match.
 *
 * Supported match types:
 * - [STANDARD]
 * - [ADVANCED]
 *
 * You can read more about how each match type works below.
 */
enum class MatchType {

    // The Standard match type is a normal match, 3x3 board, 2 players, no special rules.
    // Each player has 15 seconds to make a move.
    STANDARD,

    // The Advanced match type starts with a 3x3 board, and increases in size after each round until round 10.
    // At the end, the board will be 12x12. The player who wins the most rounds wins the match.
    //
    // Also, there's a counter informing the seconds the player has to make a move, and this time decreases after each round.
    // This counter starts at 15 seconds.
    ADVANCED

}