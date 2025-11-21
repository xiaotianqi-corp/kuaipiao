package org.xiaotianqi.kuaipiao.enums

import kotlinx.serialization.Serializable

@Serializable
enum class TrendDirection {
    INCREASING,
    DECREASING,
    STABLE,
    VOLATILE
}
