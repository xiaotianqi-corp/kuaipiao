package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.openai")
object OpenAIConfig {
    @ConfigurationProperty("api_key")
    var apiKey: String = ""

    @ConfigurationProperty("base_url")
    var baseUrl: String = "https://api.openai.com/v1"

    @ConfigurationProperty("model")
    var model: String = "gpt-4"

    @ConfigurationProperty("timeout_ms")
    var timeoutMs: Long = 2500

    @ConfigurationProperty("max_retries")
    var maxRetries: Int = 2
}