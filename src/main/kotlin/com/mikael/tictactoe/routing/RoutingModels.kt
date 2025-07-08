package com.mikael.tictactoe.routing

import kotlinx.serialization.Serializable

// Request models - Start
// Request models - End

// Response models - Start
/**
 * Base response model. All response models extend this model.
 *
 * @property success Response success status.
 */
@Serializable
@Suppress("WARNINGS")
open class Response(val success: Boolean)

/**
 * Error response model. Used when a request fails.
 *
 * @property error Error code to be rendered as a message in frontend.
 * @see Response
 */
@Serializable
data class ErrorResponse(val error: String) : Response(false)
// Response models - End