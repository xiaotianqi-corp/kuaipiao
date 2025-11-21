package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.anthropic")
object AnthropicConfig {
    @ConfigurationProperty("api_key")
    var apiKey: String = ""

    @ConfigurationProperty("base_url")
    var baseUrl: String = "https://api.anthropic.com/v1"

    @ConfigurationProperty("model")
    var model: String = "claude-3-sonnet-20240229"

    @ConfigurationProperty("timeout_ms")
    var timeoutMs: Long = 4000
}