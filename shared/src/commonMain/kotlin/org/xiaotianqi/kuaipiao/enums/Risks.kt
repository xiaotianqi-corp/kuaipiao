package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.Serializable

@Serializable
enum class RiskComparison {
    HIGHER,
    SIMILAR,
    LOWER
}

@Serializable
enum class RiskLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
enum class RiskType {
    FINANCIAL,
    OPERATIONAL,
    COMPLIANCE,
    MARKET,
    CREDIT,
    LIQUIDITY
}

@Serializable
enum class RiskPatternType {
    SUDDEN_INCREASE,
    ROUND_AMOUNTS,
    AFTER_HOURS,
    FREQUENT_SMALL_TRANSACTIONS,
    SAME_DAY_MULTIPLE,
    UNUSUAL_VENDOR
}
