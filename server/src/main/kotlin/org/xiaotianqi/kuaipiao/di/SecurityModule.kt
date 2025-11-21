package org.xiaotianqi.kuaipiao.di

import org.koin.dsl.module
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.config.ApiConfig

val SecurityModule = module {
    single {
        JwtService(
            secret = ApiConfig.jwtSecret,
            issuer = ApiConfig.jwtIssuer,
            audience = ApiConfig.jwtAudience
        )
    }
}
