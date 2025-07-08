package com.mikael.tictactoe.enums

import com.mikael.tictactoe.enums.MatchType.ADVANCED
import com.mikael.tictactoe.enums.MatchType.STANDARD
import kotlinx.serialization.Serializable

/**
 * Represents the type of a tic-tac-toe match.
 *
 * Supported match types:
 * - [STANDARD]
 * - [ADVANCED]
 *
 * You can read more about how each match type works below.
 */
@Serializable
enum class MatchType {

    // The Standard match type is a normal match, 3x3 board, 2 players, no special rules.
    //
    // Each player has 15 seconds to make a move.
    STANDARD,

    // The Advanced match type starts with a 3x3 board, and increases in size after each round until round 10.
    // At the end, the board will be 12x12. The player who wins the most rounds wins the match. 2 players.
    //
    // Also, there's a counter informing the seconds the player has to make a move, and this time decreases after each round.
    // This counter starts at 15 seconds.
    ADVANCED

}