package org.xiaotianqi.kuaipiao.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object DateTimeUtils {
    @OptIn(ExperimentalTime::class)
    fun currentMillis(): Long = Clock.System.now().toEpochMilliseconds()
}