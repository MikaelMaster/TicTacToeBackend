package com.mikael.tictactoe.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mikael.tictactoe.dotenv
import com.mikael.tictactoe.routing.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.util.*
import java.util.concurrent.TimeUnit

// JWT Token - Start
internal const val JWT_TOKEN_AUDIENCE = "tic-tac-toe-game"
internal const val JWT_TOKEN_ISSUER = "tic-tac-toe-backend"
internal val JWT_TOKEN_SECRET = dotenv["JWT_TOKEN_SECRET"]!!

/**
 * Data class for storing the generated [JWT] access token and its id.
 */
internal data class GeneratedJWT(val token: String, val tokenId: String)

/**
 * Generates a [JWT] access token with the given parameters.
 *
 * @param userId the user id to be included in the [JWT] access token.
 * @param expiresAt the expiration time of the [JWT] access token in milliseconds since epoch.
 */
internal fun generateJWTAccessToken(userId: Long, expiresAt: Long): GeneratedJWT {
    val tokenId = UUID.randomUUID().toString()
    val token = JWT.create()
        .withAudience(JWT_TOKEN_AUDIENCE)
        .withIssuer(JWT_TOKEN_ISSUER)
        .withJWTId(tokenId)
        .withClaim("userId", userId)
        .withExpiresAt(Date(expiresAt))
        .sign(Algorithm.HMAC256(JWT_TOKEN_SECRET))
    return GeneratedJWT(token, tokenId)
}
// JWT Token - End

// Cache for storing logged in users tokens.
val jwtTokenCache: Cache<Long, String> = Caffeine.newBuilder() // UserID -> TokenID (UUID)
    .expireAfterWrite(30, TimeUnit.DAYS)
    .build()

/**
 * Configures the security settings for the application.
 */
internal fun Application.configureSecurity() {
    install(StatusPages) {
        exception<Throwable> { call, exception ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(exception.message ?: "internal_error")
            )
        }
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "tic-tac-toe-game"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_TOKEN_SECRET))
                    .withAudience(JWT_TOKEN_AUDIENCE)
                    .withIssuer(JWT_TOKEN_ISSUER)
                    .withClaimPresence("userId")
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asLong()!!
                val tokenId = credential.jwtId!!
                if (jwtTokenCache.getIfPresent(userId) == tokenId) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid_access_token"))
            }
        }
    }
}