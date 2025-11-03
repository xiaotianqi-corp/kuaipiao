package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("redis")
object RedisConfig {
    @ConfigurationProperty("connection.string")
    var connectionString: String = "redis://localhost:6379"
}
