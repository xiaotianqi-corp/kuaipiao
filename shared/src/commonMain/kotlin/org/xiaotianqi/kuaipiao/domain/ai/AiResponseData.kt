package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.AiProvider

@Serializable
data class AiResponse(
    val provider: AiProvider,
    val content: String,
    val tokensUsed: Int? = null,
    val cached: Boolean = false,
    val processingTimeMs: Long,
    val confidence: Double? = null,
    val metadata: Map<String, String> = emptyMap()
)