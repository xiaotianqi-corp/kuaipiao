package org.xiaotianqi.kuaipiao.domain.processing

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class ProcessingHistoryItem(
    val id: String,
    val fileType: FileType,
    val documentType: String,
    val fileName: String,
    val confidence: Double,
    val processingTime: Long,
    val status: String,
    val createdAt: Instant = Clock.System.now()
)

@Serializable
data class ProcessingStats(
    val totalProcessed: Int,
    val successful: Int,
    val failed: Int,
    val averageConfidence: Double,
    val averageProcessingTime: Long
)