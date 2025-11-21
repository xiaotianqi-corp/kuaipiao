package org.xiaotianqi.kuaipiao.core.security

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.config.ApiConfig

@Single
class JwtProvider(private val apiConfig: ApiConfig) {

    fun get(): JwtService =
        JwtService(
            secret = apiConfig.jwtSecret,
            issuer = apiConfig.jwtIssuer,
            audience = apiConfig.jwtAudience
        )
}
