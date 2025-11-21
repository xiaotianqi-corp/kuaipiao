package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.Serializable

@Serializable
enum class UrgencyLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
enum class ConfidentialityLevel {
    PUBLIC,
    INTERNAL,
    CONFIDENTIAL,
    RESTRICTED
}

@Serializable
enum class AutomationLevel {
    MANUAL,
    ASSISTED,
    SEMI_AUTOMATIC,
    FULL_AUTOMATION
}