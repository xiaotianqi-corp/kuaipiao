package org.xiaotianqi.kuaipiao.data.daos.ai

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.DocumentProcessingTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.DocumentProcessingEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.InvoiceDataEntity
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.processing.*
import org.xiaotianqi.kuaipiao.enums.FileType
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

@Single
@ExperimentalTime
class DocumentProcessingDao {

    suspend fun saveInvoiceProcessing(
        userId: String,
        companyId: String,
        result: InvoiceProcessingResult
    ): String = transaction {
        val entity = DocumentProcessingEntity.new {
            this.userId = userId
            this.companyId = companyId
            fileType = "PDF"
            documentType = "INVOICE"
            fileName = "invoice_${result.invoiceId}"
            fileSize = 0L
            confidence = result.confidence
            processingTime = result.processingTime
            aiProvider = result.aiProvider
            extractedFields = calculateExtractedFields(result)
            status = if (result.validationErrors.isEmpty()) "SUCCESS" else "VALIDATION_ERROR"
            createdAt = Instant.now()
        }

        InvoiceDataEntity.new {
            processing = entity
            invoiceNumber = result.extractedData.number
            issueDate = result.extractedData.date.toString()
            emitterRuc = result.extractedData.providerId.taxId ?: ""
            receiverRuc = result.extractedData.customerId.documentNumber ?: ""
            subtotal = result.extractedData.subtotal.toDoubleOrNull()?.toBigDecimal() ?: java.math.BigDecimal.ZERO
            total = result.extractedData.total.toDoubleOrNull()?.toBigDecimal() ?: java.math.BigDecimal.ZERO
            currency = result.extractedData.currency
            itemsCount = result.extractedData.items.size
            taxesAmount = result.extractedData.taxes.sumOf { it.amount }.toBigDecimal()
            rawText = result.extractedData.notes
        }

        entity.id.value.toString()
    }

    suspend fun getProcessingHistory(
        userId: String,
        limit: Int = 50,
        offset: Int = 0
    ): List<ProcessingHistoryItem> = transaction {
        DocumentProcessingTable
            .selectAll()
            .where { DocumentProcessingTable.userId eq userId }
            .orderBy(DocumentProcessingTable.createdAt to SortOrder.DESC)
            .limit(limit).offset(offset.toLong())
            .map { row ->
                ProcessingHistoryItem(
                    id = row[DocumentProcessingTable.id].value.toString(),
                    fileType = FileType.valueOf(row[DocumentProcessingTable.fileType]),
                    documentType = row[DocumentProcessingTable.documentType],
                    fileName = row[DocumentProcessingTable.fileName],
                    confidence = row[DocumentProcessingTable.confidence],
                    processingTime = row[DocumentProcessingTable.processingTime],
                    status = row[DocumentProcessingTable.status],
                    createdAt = row[DocumentProcessingTable.createdAt].toKotlinInstant()
                )
            }
    }

    suspend fun getCompanyProcessingStats(companyId: String): ProcessingStats = transaction {
        val total = DocumentProcessingTable
            .selectAll()
            .where { DocumentProcessingTable.companyId eq companyId }
            .count()

        val success = DocumentProcessingTable
            .selectAll()
            .where {
                (DocumentProcessingTable.companyId eq companyId) and
                        (DocumentProcessingTable.status eq "SUCCESS")
            }
            .count()

        val avgConfidenceResult = DocumentProcessingTable
            .select(DocumentProcessingTable.confidence.avg())
            .where { DocumentProcessingTable.companyId eq companyId }
            .firstOrNull()
        val avgConfidence = avgConfidenceResult?.get(DocumentProcessingTable.confidence.avg())?.let {
            (it as? Number)?.toDouble()
        } ?: 0.0

        val avgProcessingTimeResult = DocumentProcessingTable
            .select(DocumentProcessingTable.processingTime.avg())
            .where { DocumentProcessingTable.companyId eq companyId }
            .firstOrNull()
        val avgProcessingTime = avgProcessingTimeResult?.get(DocumentProcessingTable.processingTime.avg())?.let {
            (it as? Number)?.toLong()
        } ?: 0L

        ProcessingStats(
            totalProcessed = total.toInt(),
            successful = success.toInt(),
            failed = (total - success).toInt(),
            averageConfidence = avgConfidence,
            averageProcessingTime = avgProcessingTime
        )
    }

    private fun calculateExtractedFields(result: InvoiceProcessingResult): Int {
        var count = 0
        with(result.extractedData) {
            if (number.isNotBlank()) count++
            if (date.toString().isNotBlank()) count++
            if (providerId.taxId?.isNotBlank() == true) count++
            if (customerId.documentNumber?.isNotBlank() == true) count++
            if ((subtotal.toDoubleOrNull() ?: 0.0) > 0) count++
            if ((total.toDoubleOrNull() ?: 0.0) > 0) count++
            if (currency.isNotBlank()) count++
            count += items.size
            count += taxes.size
        }
        return count
    }
}