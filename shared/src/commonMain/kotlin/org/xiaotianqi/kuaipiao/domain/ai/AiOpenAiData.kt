package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    val stream: Boolean = false
)

@Serializable
data class OpenAiResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    @SerialName("object")
    val objectType: String,
    val usage: Usage
)

@Serializable
data class ClassificationResult(
    val category: String,
    val confidence: Double,
    val alternatives: List<String>
)

@Serializable
data class SentimentAnalysis(
    val sentiment: String,
    val confidence: Double,
    val aspects: List<String>,
    val emotions: List<String>
)

@Serializable
data class UsageStats(
    val totalTokens: Int = 0,
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalCost: Double = 0.0
)