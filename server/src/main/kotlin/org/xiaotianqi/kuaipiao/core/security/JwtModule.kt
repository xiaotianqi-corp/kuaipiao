package org.xiaotianqi.kuaipiao.core.security

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.config.ApiConfig

@Module
class JwtModule {

    @Single
    fun jwtService(config: ApiConfig): JwtService =
        JwtService(
            secret = config.jwtSecret,
            issuer = config.jwtIssuer,
            audience = config.jwtAudience
        )
}
