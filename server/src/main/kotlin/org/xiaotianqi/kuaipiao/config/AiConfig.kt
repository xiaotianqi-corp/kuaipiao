package org.xiaotianqi.kuaipiao.config

import org.xiaotianqi.kuaipiao.config.ai.AnthropicConfig
import org.xiaotianqi.kuaipiao.config.ai.CacheConfig
import org.xiaotianqi.kuaipiao.config.ai.DeepSeekConfig
import org.xiaotianqi.kuaipiao.config.ai.GoogleVisionConfig
import org.xiaotianqi.kuaipiao.config.ai.OpenAIConfig
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

object AiConfig {
    fun getOpenAITimeout() = OpenAIConfig.timeoutMs.milliseconds
    fun getDeepSeekTimeout() = DeepSeekConfig.timeoutMs.milliseconds
    fun getGoogleTimeout() = GoogleVisionConfig.timeoutMs.milliseconds
    fun getAnthropicTimeout() = AnthropicConfig.timeoutMs.milliseconds
    fun getCacheTtl() = CacheConfig.ttlHours.hours
}