package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    @SerialName("max_tokens")
    val maxTokens: Int,
    val temperature: Double = 1.0,
    val system: String? = null
)

@Serializable
data class AnthropicMessage(
    val role: String,
    val content: String
)

@Serializable
data class AnthropicResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<AnthropicContent>,
    val model: String,
    @SerialName("stop_reason")
    val stopReason: String? = null,
    val usage: AnthropicUsage
)

@Serializable
data class AnthropicContent(
    val type: String,
    val text: String
)

@Serializable
data class AnthropicUsage(
    @SerialName("input_tokens")
    val inputTokens: Int,
    @SerialName("output_tokens")
    val outputTokens: Int
)