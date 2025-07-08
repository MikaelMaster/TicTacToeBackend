package com.mikael.tictactoe.enums

import com.mikael.tictactoe.enums.MatchResult.*


/**
 * Represents the result of a match.
 *
 * Supported match results:
 * * [WIN] - The player won the match.
 * * [LOSS] - The player lost the match.
 * * [TIE] - The match ended in a tie.
 *
 * @see MatchResult
 */
enum class MatchResult {

    WIN, LOSS, TIE

}