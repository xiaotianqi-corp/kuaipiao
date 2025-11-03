package org.xiaotianqi.kuaipiao.core.logic

import java.time.Instant

object DatetimeUtils {
    fun currentMillis(): Long = System.currentTimeMillis()

    fun currentJavaInstant(): Instant = Instant.now()
}