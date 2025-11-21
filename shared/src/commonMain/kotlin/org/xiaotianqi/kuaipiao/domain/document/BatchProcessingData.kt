package org.xiaotianqi.kuaipiao.domain.document

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationItem
import org.xiaotianqi.kuaipiao.domain.classification.ClassificationSummary
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class BatchProcessingResult(
    val successful: List<InvoiceProcessingResult>,
    val failed: List<ProcessingError>,
    val summary: ProcessingSummary
)

@Serializable
data class ProcessingError(
    val fileIndex: Int,
    val error: String,
    val fileType: FileType
)

@Serializable
data class ProcessingSummary(
    val totalFiles: Int,
    val processed: Int,
    val failed: Int,
    val averageConfidence: Double
)

@Serializable
@ExperimentalTime
data class BatchClassificationResult(
    val items: List<ClassificationItem>,
    val summary: ClassificationSummary,
    val processingMetadata: BatchProcessingMetadata
)

@Serializable
@ExperimentalTime
data class BatchProcessingMetadata(
    val batchId: String,
    val startedAt: Instant,
    val completedAt: Instant,
    val totalProcessingTime: Long,
    val itemsPerSecond: Double
)