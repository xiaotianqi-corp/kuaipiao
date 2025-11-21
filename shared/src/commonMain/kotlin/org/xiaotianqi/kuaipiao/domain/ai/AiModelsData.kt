package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Message(
    val role: String,
    val content: String,
    val name: String? = null
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)