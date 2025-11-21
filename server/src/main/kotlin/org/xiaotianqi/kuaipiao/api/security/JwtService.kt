package org.xiaotianqi.kuaipiao.api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

data class JwtPayload(
    val userId: String,
    val roles: List<String>
)

class JwtService(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
) {

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(
        userId: String,
        roles: List<String> = emptyList(),
        expiresInMs: Long = 24 * 60 * 60 * 1000 // 24h
    ): String {
        val now = Date()
        val expires = Date(now.time + expiresInMs)

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(expires)
            .sign(algorithm)
    }

    fun verifier() =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

    fun verifyToken(token: String): JwtPayload? {
        return try {
            val decoded: DecodedJWT = verifier().verify(token)
            JwtPayload(
                userId = decoded.getClaim("userId").asString(),
                roles = decoded.getClaim("roles").asList(String::class.java) ?: emptyList()
            )
        } catch (_: Exception) {
            null
        }
    }
}
