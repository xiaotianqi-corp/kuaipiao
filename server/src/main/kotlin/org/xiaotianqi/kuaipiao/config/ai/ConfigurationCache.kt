package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.cache")
object CacheConfig {
    @ConfigurationProperty("enabled")
    var enabled: Boolean = true

    @ConfigurationProperty("ttl_hours")
    var ttlHours: Long = 24

    @ConfigurationProperty("max_size")
    var maxSize: Int = 10000
}