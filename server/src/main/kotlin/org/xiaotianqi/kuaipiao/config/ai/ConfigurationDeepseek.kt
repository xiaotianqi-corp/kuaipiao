package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.deepseek")
object DeepSeekConfig {
    @ConfigurationProperty("api_key")
    var apiKey: String = ""

    @ConfigurationProperty("base_url")
    var baseUrl: String = "https://api.deepseek.com/v1"

    @ConfigurationProperty("model")
    var model: String = "deepseek-chat"

    @ConfigurationProperty("timeout_ms")
    var timeoutMs: Long = 3000
}