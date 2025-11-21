package org.xiaotianqi.kuaipiao.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object DateTimeUtils {

    @OptIn(ExperimentalTime::class)
    fun currentMillis(): Long = Clock.System.now().toEpochMilliseconds()

    @OptIn(ExperimentalTime::class)
    fun isExpired(iat: Long, maxAgeMs: Long = 24 * 60 * 60 * 1000): Boolean {
        val now = currentMillis()
        return (now - iat) > maxAgeMs
    }
}
