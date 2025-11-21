package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.core.logic.ai.AiOrchestrator
import org.xiaotianqi.kuaipiao.core.logic.ai.ResponseValidator
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingDocument
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import org.xiaotianqi.kuaipiao.enums.AutomationLevel
import org.xiaotianqi.kuaipiao.domain.metrics.AutomationMetrics
import org.xiaotianqi.kuaipiao.domain.reconciliation.BatchReconciliationResult
import org.xiaotianqi.kuaipiao.domain.reconciliation.ReconciliationError
import org.xiaotianqi.kuaipiao.domain.reconciliation.ReconciliationSummary
import org.xiaotianqi.kuaipiao.enums.FileType
import java.math.BigDecimal
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class ReconcileAccountingUseCase(
    private val aiOrchestrator: AiOrchestrator,
    private val validator: ResponseValidator,
    private val aiDBI: AiDBI
) {

    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileType: FileType,
        userId: String,
        companyId: String,
        country: String
    ): Result<AccountingReconciliationResult> {

        logger.info { "Starting accounting reconciliation for company: $companyId" }

        return try {
            validateAccountingDocument(fileBytes, fileType)

            val historicalPatterns = aiDBI.getAccountingPatterns(companyId)

            val reconciliation = aiOrchestrator.reconcileAccountingDocument(
                fileBytes = fileBytes,
                fileType = fileType,
                companyId = companyId,
                historicalPatterns = historicalPatterns
            )

            validateReconciliation(reconciliation)

            aiDBI.saveAccountingReconciliation(
                userId = userId,
                companyId = companyId,
                result = reconciliation
            )

            logger.info {
                "Reconciliation completed: ${reconciliation.automationLevel} automation " +
                        "(trust: ${reconciliation.confidenceScore})"
            }

            Result.success(reconciliation)

        } catch (e: Exception) {
            logger.error(e) { "Error in accounting reconciliation" }
            Result.failure(e)
        }
    }

    suspend fun reconcileBatch(
        documents: List<AccountingDocument>,
        userId: String,
        companyId: String,
        country: String
    ): BatchReconciliationResult {

        val results = mutableListOf<AccountingReconciliationResult>()
        val errors = mutableListOf<ReconciliationError>()

        documents.forEachIndexed { index, document ->
            try {
                val result = invoke(
                    fileBytes = document.fileBytes,
                    fileType = document.fileType,
                    userId = userId,
                    companyId = companyId,
                    country = country
                ).getOrThrow()

                results.add(result)
            } catch (e: Exception) {
                errors.add(
                    ReconciliationError(
                        documentIndex = index,
                        documentType = document.documentType,
                        error = e.message ?: "Unknown error"
                    )
                )
            }
        }

        return BatchReconciliationResult(
            successful = results,
            failed = errors,
            summary = ReconciliationSummary(
                totalDocuments = documents.size,
                reconciled = results.size,
                failed = errors.size,
                automationRate = calculateAutomationRate(results)
            )
        )
    }

    suspend fun getReconciliationHistory(
        companyId: String,
        limit: Int = 50
    ): List<AccountingReconciliationResult> {

        return aiDBI.getReconciliationHistory(companyId, limit)
    }

    suspend fun calculateAutomationMetrics(
        companyId: String,
        periodDays: Int = 30
    ): AutomationMetrics {

        val history = getReconciliationHistory(companyId, 1000)
        val periodHistory = history.filter {
            val docDate = java.time.LocalDate.parse(it.reconciliation.documentDate)
            docDate.isAfter(java.time.LocalDate.now().minusDays(periodDays.toLong()))
        }

        return AutomationMetrics(
            companyId = companyId,
            periodDays = periodDays.days,
            totalReconciliations = periodHistory.size,
            fullAutomation = periodHistory.count { it.automationLevel == AutomationLevel.FULL_AUTOMATION },
            partialAutomation = periodHistory.count { it.automationLevel == AutomationLevel.SEMI_AUTOMATIC },
            manualProcessing = periodHistory.count { it.automationLevel == AutomationLevel.MANUAL },
            averageConfidence = periodHistory.map { it.confidenceScore }.average(),
            automationRate = calculateAutomationRate(periodHistory),
            timeSaved = calculateTimeSaved(periodHistory).minutes
        )
    }

    private fun validateAccountingDocument(fileBytes: ByteArray, fileType: FileType) {
        if (fileBytes.isEmpty()) {
            throw IllegalArgumentException("Empty accounting document")
        }

        if (fileBytes.size > 15 * 1024 * 1024) {
            throw IllegalArgumentException("Document too large for processing")
        }

        val allowedTypes = setOf(FileType.PDF, FileType.IMAGE, FileType.EXCEL)

        if (fileType !in allowedTypes) {
            throw IllegalArgumentException("File type not supported for accounting reconciliation: $fileType")
        }
    }

    private fun validateReconciliation(reconciliation: AccountingReconciliationResult) {

        if (reconciliation.suggestedEntries.isEmpty()) {
            throw IllegalArgumentException("No journal entry suggestions could be generated")
        }

        if (reconciliation.confidenceScore < 0.3) {
            throw IllegalArgumentException("Confidence too low for automation: ${reconciliation.confidenceScore}")
        }

        val totalSuggested = reconciliation.suggestedEntries
            .sumOf { it.amount }
            .toBigDecimal()

        val documentTotalDouble = reconciliation.extractedData.extractedData.total

        val documentTotal = documentTotalDouble.toBigDecimal()

        val tolerance = BigDecimal("0.01")

        val difference = totalSuggested.subtract(documentTotal).abs()

        if (difference > tolerance) {
            throw IllegalArgumentException(
                "Discrepancy in amounts: suggested=$totalSuggested, document=$documentTotal"
            )
        }
    }


    private fun calculateAutomationRate(results: List<AccountingReconciliationResult>): Double {
        if (results.isEmpty()) return 0.0

        val automated = results.count {
            it.automationLevel == AutomationLevel.FULL_AUTOMATION ||
                    it.automationLevel == AutomationLevel.SEMI_AUTOMATIC
        }

        return automated.toDouble() / results.size
    }

    private fun calculateTimeSaved(results: List<AccountingReconciliationResult>): Long {
        var timeSaved = 0L

        results.forEach { result ->
            timeSaved += when (result.automationLevel) {
                AutomationLevel.FULL_AUTOMATION -> 8 * 60
                AutomationLevel.SEMI_AUTOMATIC -> 5 * 60
                AutomationLevel.MANUAL -> 0
                AutomationLevel.ASSISTED -> 0
            }
        }

        return timeSaved
    }
}