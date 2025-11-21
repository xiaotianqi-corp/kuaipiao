package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DeepSeekRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.1,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    val stream: Boolean = false
)

@Serializable
data class DeepSeekResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    @SerialName("object")
    val objectType: String,
    val usage: Usage? = null
)
