package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AiProvider {
    OPENAI,
    DEEPSEEK,
    GOOGLE_VISION,
    ANTHROPIC
}