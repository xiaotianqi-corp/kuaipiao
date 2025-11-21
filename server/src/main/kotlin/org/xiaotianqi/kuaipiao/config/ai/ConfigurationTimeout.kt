package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.timeouts")
object TimeoutConfig {
    @ConfigurationProperty("total_processing_ms")
    var totalProcessingMs: Long = 8000

    @ConfigurationProperty("per_provider_ms")
    var perProviderMs: Long = 3000

    @ConfigurationProperty("cache_read_ms")
    var cacheReadMs: Long = 500
}