package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("api")
object ApiConfig {
    @ConfigurationProperty("port")
    var port: Int = 8080

    @ConfigurationProperty("cookie.secure")
    var cookieSecure: Boolean = true

    @ConfigurationProperty("session.max.age.in.seconds")
    var sessionMaxAgeInSeconds: Long = 2592000 // 30 days by default

    @ConfigurationProperty("jwt.secret")
    var jwtSecret: String = System.getenv("JWT_SECRET") ?: "your-256-bit-secret-change-in-production"

    @ConfigurationProperty("jwt.issuer")
    var jwtIssuer: String = "kuaipiao"

    @ConfigurationProperty("jwt.audience")
    var jwtAudience: String = "kuaipiao-api"
}
