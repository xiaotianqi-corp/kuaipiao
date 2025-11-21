package org.xiaotianqi.kuaipiao.core.exceptions

open class AiException(
    message: String,
    val errorCode: String = "AI_ERROR",
    val details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : RuntimeException(message)

class AiRateLimitException(
    provider: String,
    retryAfter: Long? = null
) : AiException(
    message = "Rate limit exceeded for $provider",
    errorCode = "AI_RATE_LIMIT",
    details = mapOf(
        "provider" to provider,
        "retryAfter" to (retryAfter ?: 60)
    )
)

class AiValidationException(
    message: String,
    val validationErrors: List<String> = emptyList()
) : AiException(
    message = message,
    errorCode = "AI_VALIDATION_ERROR",
    details = mapOf("validationErrors" to validationErrors)
)

class AiProviderException(
    provider: String,
    cause: Throwable? = null
) : AiException(
    message = "AI provider $provider unavailable",
    errorCode = "AI_PROVIDER_ERROR",
    details = mapOf("provider" to provider),
    cause = cause
)