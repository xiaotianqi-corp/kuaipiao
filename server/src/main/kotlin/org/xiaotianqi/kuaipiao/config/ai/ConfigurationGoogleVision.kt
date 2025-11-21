package org.xiaotianqi.kuaipiao.config.ai

import org.xiaotianqi.kuaipiao.config.core.Configuration
import org.xiaotianqi.kuaipiao.config.core.ConfigurationProperty

@Configuration("ai.google")
object GoogleVisionConfig {
    @ConfigurationProperty("api_key")
    var apiKey: String = ""

    @ConfigurationProperty("base_url")
    var baseUrl: String = "https://vision.googleapis.com"

    @ConfigurationProperty("gemini_base_url")
    var geminiBaseUrl: String = "https://generativelanguage.googleapis.com"

    @ConfigurationProperty("gemini_model")
    var geminiModel: String = "gemini-pro"

    @ConfigurationProperty("use_gemini")
    var useGemini: Boolean = true

    @ConfigurationProperty("service_account_json")
    var serviceAccountJson: String = ""
    @ConfigurationProperty("timeout_ms")
    var timeoutMs: Long = 5000
}