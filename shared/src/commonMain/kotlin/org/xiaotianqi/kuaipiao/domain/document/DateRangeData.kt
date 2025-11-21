package org.xiaotianqi.kuaipiao.domain.document

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class DateRange(
    val start: Instant,
    val end: Instant
) {
    companion object {
        fun last30Days(): DateRange {
            val now = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val startInstant = now.minus(30.days)
            val end = Clock.System.now()
            val start = Clock.System.now()
            return DateRange(start, end)
        }

        fun last90Days(): DateRange {
            val now = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val startInstant = now.minus(90.days)
            val end = Clock.System.now()
            val start = Clock.System.now()
            return DateRange(start, end)
        }
    }
}