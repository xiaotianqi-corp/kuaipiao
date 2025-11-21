package org.xiaotianqi.kuaipiao.core.logic.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.core.clients.AiClientManager
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonElement
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.xiaotianqi.kuaipiao.core.clients.ai.GoogleVisionClient
import org.xiaotianqi.kuaipiao.data.daos.benchmark.BenchmarkDao
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai.AiCacheSource
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai.get
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingDimension
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingEntry
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliation
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceCheck
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRecommendation
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.organization.CompanyHistory
import org.xiaotianqi.kuaipiao.domain.risk.RiskPattern
import org.xiaotianqi.kuaipiao.domain.trade.ExportAlert
import org.xiaotianqi.kuaipiao.domain.trade.ExportDocumentResult
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.domain.validation.CrossValidationResult
import org.xiaotianqi.kuaipiao.domain.validation.ValidationResult
import org.xiaotianqi.kuaipiao.domain.validation.DataDiscrepancy
import org.xiaotianqi.kuaipiao.enums.AccountingType
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import org.xiaotianqi.kuaipiao.enums.AlertType
import org.xiaotianqi.kuaipiao.enums.AutomationLevel
import org.xiaotianqi.kuaipiao.enums.CrossValidationStatus
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType
import org.xiaotianqi.kuaipiao.enums.LedgerBook
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import org.xiaotianqi.kuaipiao.enums.TransactionType
import java.io.ByteArrayInputStream
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class AiOrchestrator(
    private val aiClientManager: AiClientManager,
    private val googleVisionClient: GoogleVisionClient,
    private val cache: AiCacheSource,
    private val countryRuleEngine: CountryRuleEngine,
    private val taxComplianceAnalyzer: TaxComplianceAnalyzer,
    private val benchmarkDao: BenchmarkDao,
    private val companyDao: CompanyDao
) {

    suspend fun extractDocument(
        prompt: String,
        fileBytes: ByteArray,
        fileType: FileType,
        operation: String
    ): DocumentExtractionResult {

        val startMs = Clock.System.now().toEpochMilliseconds()

        return try {

            val result = aiClientManager.extractDocument(
                prompt = prompt,
                fileBytes = fileBytes,
                fileType = fileType,
                operation = operation
            )

            val processingTime = if (result.processingTimeMs > 0L)
                result.processingTimeMs
            else
                Clock.System.now().toEpochMilliseconds() - startMs

            result.copy(processingTimeMs = processingTime)

        } catch (e: Exception) {

            logger.error(e) { "extractDocument failed for operation=$operation" }

            DocumentExtractionResult(
                documentIndex = 0,
                documentType = DocumentType.IDENTIFICATION,
                success = false,
                confidence = 0.0,
                rawText = e.message ?: "",
                extractedData = emptyMap<String, JsonElement>(),
                processingTimeMs = Clock.System.now().toEpochMilliseconds() - startMs
            )
        }
    }

    private var processingStartTime: Long = 0L

    suspend fun processExportDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        exporterCountry: String,
        importerCountry: String,
        productDescriptions: List<String>
    ): ExportDocumentResult = coroutineScope {

        processingStartTime = System.currentTimeMillis()
        logger.info { "Processing export document: $exporterCountry → $importerCountry" }

        val deferredTextExtraction = async {
            extractTextFromDocument(fileBytes, fileType)
        }

        val deferredTariffClassification = async {
            classifyTariffCodes(productDescriptions, exporterCountry, importerCountry)
        }

        val deferredCountryRules = async {
            countryRuleEngine.getExportRules(exporterCountry, importerCountry)
        }

        val rawText = deferredTextExtraction.await()
        val tariffClassifications = deferredTariffClassification.await()
        val exportRules = deferredCountryRules.await()

        val structuredData = aiClientManager.processInvoiceWithOCR(
            fileBytes, fileType, exporterCountry
        )

        val complianceCheck = taxComplianceAnalyzer.validateExportCompliance(
            structuredData, tariffClassifications, exportRules
        )

        val proactiveAlerts = generateExportAlerts(
            structuredData, tariffClassifications, complianceCheck
        )

        val processingDuration = calculateProcessingTime()
        val providerName = aiClientManager.getCurrentProvider().name

        return@coroutineScope ExportDocumentResult(
            documentData = structuredData,
            tariffClassifications = tariffClassifications,
            complianceCheck = complianceCheck,
            requiredDocuments = exportRules.requiredDocuments,
            alerts = proactiveAlerts,
            riskLevel = calculateExportRisk(complianceCheck, proactiveAlerts),
            processingTime = processingDuration,
            aiProvider = providerName,
            rawText = rawText
        )
    }

    suspend fun reconcileAccountingDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        companyId: String,
        historicalPatterns: List<AccountingPattern>
    ): AccountingReconciliationResult = coroutineScope {

        processingStartTime = System.currentTimeMillis()
        logger.info { "Starting accounting reconciliation for company: $companyId" }

        val rawText = extractTextFromDocument(fileBytes, fileType)

        val documentData = aiClientManager.processInvoiceWithOCR(
            fileBytes, fileType, "EC"
        )

        val reconciliation = aiClientManager.reconcileAccounting(
            documentData.extractedData,
            historicalPatterns
        )

        val crossValidation = validateWithHistoricalPatterns(
            reconciliation, historicalPatterns
        )

        val accountingEntries = generateAccountingEntries(
            documentData, reconciliation, crossValidation
        )

        return@coroutineScope AccountingReconciliationResult(
            extractedData = documentData,
            reconciliation = reconciliation,
            crossValidation = crossValidation,
            suggestedEntries = accountingEntries,
            confidenceScore = calculateReconciliationConfidence(reconciliation, crossValidation),
            automationLevel = determineAutomationLevel(reconciliation, crossValidation)
        )
    }

    suspend fun analyzeComplianceRisk(
        companyId: String,
        transactions: List<TransactionData>,
        period: DateRange
    ): ComplianceRiskAnalysis = coroutineScope {

        processingStartTime = System.currentTimeMillis()
        logger.info { "Analyzing compliance risk for: $companyId" }

        val deferredHistoricalAnalysis = async {
            analyzeHistoricalPatterns(transactions, period)
        }

        val deferredRegulatoryChanges = async {
            countryRuleEngine.getRecentRegulatoryChanges(period)
        }

        val deferredIndustryBenchmarks = async {
            taxComplianceAnalyzer.getIndustryBenchmarks(companyId)
        }

        val riskPatterns = detectRiskPatterns(transactions)

        val aggregatedTransaction = aggregateTransactions(transactions)
        val companyHistory = CompanyHistory.fromTransactions(companyId, transactions, period)

        val riskAnalysis = aiClientManager.analyzeComplianceRisk(
            aggregatedTransaction,
            companyHistory
        )

        val historicalPatterns = deferredHistoricalAnalysis.await()
        val regulatoryChanges = deferredRegulatoryChanges.await()
        val industryBenchmarks = deferredIndustryBenchmarks.await()


        val riskScore = calculateComplianceRiskScore(
            riskAnalysis, riskPatterns, regulatoryChanges
        )

        val recommendations = generateComplianceRecommendations(
            riskAnalysis, riskScore, industryBenchmarks
        )

        return@coroutineScope riskAnalysis.copy(
            riskScore = riskScore,
            recommendations = recommendations
        )
    }

    private suspend fun extractTextFromDocument(
        fileBytes: ByteArray,
        fileType: FileType
    ): String {
        val cacheKey = "ocr_${fileBytes.contentHashCode()}_${fileType.name}"

        return cache.get(cacheKey) ?: run {
            val text = when (fileType) {
                FileType.PDF -> googleVisionClient.extractTextFromPdf(fileBytes)
                FileType.IMAGE -> googleVisionClient.extractTextFromImage(fileBytes)
                FileType.EXCEL -> extractTextFromExcel(fileBytes)
                else -> throw IllegalArgumentException("Unsupported file type: $fileType")
            }
            cache.set(cacheKey, text, 24.hours.inWholeMilliseconds.toString())
            text
        }
    }

    private fun extractTextFromExcel(fileBytes: ByteArray): String {
        return try {
            val workbook = WorkbookFactory.create(ByteArrayInputStream(fileBytes))
            val textBuilder = StringBuilder()

            workbook.forEach { sheet ->
                sheet.forEach { row ->
                    row.forEach { cell ->
                        textBuilder.append(cell.toString()).append(" ")
                    }
                    textBuilder.append("\n")
                }
            }

            workbook.close()
            textBuilder.toString()
        } catch (e: Exception) {
            logger.error(e) { "Error extracting text from Excel" }
            throw IllegalArgumentException("The Excel file could not be processed: ${e.message}")
        }
    }

    private suspend fun classifyTariffCodes(
        productDescriptions: List<String>,
        originCountry: String,
        destinationCountry: String
    ): List<TariffClassification> {
        return productDescriptions.map { description ->
            aiClientManager.classifyTariffCode(description, originCountry, destinationCountry)
        }
    }

    private fun detectRiskPatterns(transactions: List<TransactionData>): List<RiskPattern> {
        return taxComplianceAnalyzer.analyzeTransactionPatterns(transactions)
    }

    private fun generateExportAlerts(
        documentData: InvoiceProcessingResult,
        tariffClassifications: List<TariffClassification>,
        compliance: ComplianceCheck
    ): List<ExportAlert> {
        val alerts = mutableListOf<ExportAlert>()

        if (compliance.missingDocuments.isNotEmpty()) {
            alerts.add(
                ExportAlert(
                    type = AlertType.MISSING_DOCUMENTATION,
                    severity = SeverityStatus.HIGH,
                    message = "Missing required documents: ${compliance.missingDocuments.joinToString()}",
                    actionRequired = true
                )
            )
        }

        tariffClassifications.forEach { classification ->
            if (classification.confidence < 0.8) {
                alerts.add(
                    ExportAlert(
                        type = AlertType.LOW_CONFIDENCE_TARIFF,
                        severity = SeverityStatus.MEDIUM,
                        message = "Low confidence in tariff classification: ${classification.productDescription}",
                        actionRequired = true
                    )
                )
            }
        }

        val recentChanges = countryRuleEngine.getRecentRegulatoryChanges(
            DateRange.last30Days()
        )
        if (recentChanges.isNotEmpty()) {
            alerts.add(
                ExportAlert(
                    type = AlertType.REGULATORY_CHANGE,
                    severity = SeverityStatus.MEDIUM,
                    message = "${recentChanges.size} recent regulatory changes detected",
                    actionRequired = false
                )
            )
        }

        return alerts
    }

    private fun calculateProcessingTime(): Long {
        return System.currentTimeMillis() - processingStartTime
    }

    private fun calculateExportRisk(
        compliance: ComplianceCheck,
        alerts: List<ExportAlert>
    ): RiskLevel {
        val highSeverityCount = alerts.count { it.severity == SeverityStatus.HIGH }
        val mediumSeverityCount = alerts.count { it.severity == SeverityStatus.MEDIUM }

        return when {
            !compliance.isCompliant || highSeverityCount > 0 -> RiskLevel.HIGH
            mediumSeverityCount > 2 -> RiskLevel.MEDIUM
            mediumSeverityCount > 0 -> RiskLevel.LOW
            else -> RiskLevel.NONE
        }
    }

    private fun validateWithHistoricalPatterns(
        reconciliation: AccountingReconciliation,
        historicalPatterns: List<AccountingPattern>
    ): CrossValidationResult {

        val discrepancies = mutableListOf<DataDiscrepancy>()
        val deviations = mutableListOf<DataDiscrepancy>()

        reconciliation.suggestedAccounts.forEach { suggested ->
            val matchingPattern = historicalPatterns.find { it.accountCode == suggested.accountCode }

            if (matchingPattern == null) {
                deviations.add(
                    DataDiscrepancy(
                        field = "accountCode",
                        sourceAValue = "Pattern exists in historical data",
                        sourceBValue = suggested.accountCode,
                        message = "Account ${suggested.accountCode} it has no historical pattern",
                        severity = SeverityStatus.HIGH
                    )
                )
            }
        }

        val matchRate = if (reconciliation.suggestedAccounts.isNotEmpty()) {
            (reconciliation.suggestedAccounts.size - deviations.size).toDouble() /
                    reconciliation.suggestedAccounts.size
        } else 0.0

        val status = if (matchRate > 0.8) {
            CrossValidationStatus.PASSED
        } else {
            CrossValidationStatus.REVIEW_REQUIRED
        }

        return CrossValidationResult(
            matchStatus = status,
            discrepancies = discrepancies,
            deviations = deviations,
            matchRate = matchRate,
            overallValidationResult = if (status == CrossValidationStatus.PASSED)
                ValidationResult.VALID
            else
                ValidationResult.WARNING,
            confidenceScore = matchRate
        )
    }


    private fun generateAccountingEntries(
        documentData: InvoiceProcessingResult,
        reconciliation: AccountingReconciliation,
        crossValidation: CrossValidationResult
    ): List<AccountingEntry> {

        return reconciliation.suggestedAccounts.map { suggested ->

            val money = suggested.amount
            val isDebit = suggested.amount.baseAmount > 0

            AccountingEntry(
                entryId = "ACC-${Clock.System.now().toEpochMilliseconds()}-${suggested.accountCode}",
                journalId = documentData.invoiceId,
                ledgerBook = LedgerBook.GENERAL,
                type = listOf(if (isDebit) AccountingType.DEBIT else AccountingType.CREDIT),
                accountCode = suggested.accountCode,
                accountName = suggested.accountName,
                debit = if (isDebit) money.baseAmount else 0.0,
                credit = if (!isDebit) money.baseAmount else 0.0,
                currency = money.currency,
                amount = money.amount,
                foreignAmount =
                    if (money.exchangeRateToBase != 1.0) money.amount else null,
                exchangeRate = money.exchangeRateToBase,
                postingDate = reconciliation.documentDate,
                transactionDate = reconciliation.documentDate,
                reference = documentData.invoiceId,
                description = suggested.description,
                dimensions = AccountingDimension(
                    costCenter = reconciliation.costCenter,
                    projectCode = reconciliation.projectCode
                ),
                provenance = "AI-Reconciliation",
                confidence = suggested.confidence * crossValidation.matchRate,
                createdAt = Clock.System.now(),
                createdBy = documentData.aiProvider
            )
        }
    }

    private fun calculateReconciliationConfidence(
        reconciliation: AccountingReconciliation,
        crossValidation: CrossValidationResult
    ): Double {
        val avgAccountConfidence = reconciliation.suggestedAccounts
            .mapNotNull { it.confidence }
            .average()

        return (avgAccountConfidence + crossValidation.matchRate) / 2.0
    }

    private fun determineAutomationLevel(
        reconciliation: AccountingReconciliation,
        crossValidation: CrossValidationResult
    ): AutomationLevel {
        val confidence = calculateReconciliationConfidence(reconciliation, crossValidation)

        return when {
            confidence > 0.95 && crossValidation.deviations.isEmpty() -> AutomationLevel.FULL_AUTOMATION
            confidence > 0.85 -> AutomationLevel.SEMI_AUTOMATIC
            confidence > 0.70 -> AutomationLevel.ASSISTED
            else -> AutomationLevel.MANUAL
        }
    }

    private suspend fun analyzeHistoricalPatterns(
        transactions: List<TransactionData>,
        period: DateRange
    ): List<RiskPattern> {
        return taxComplianceAnalyzer.analyzeHistoricalRiskPatterns(transactions, period)
    }

    private fun aggregateTransactions(transactions: List<TransactionData>): TransactionData {
        val totalAmount = transactions.sumOf { it.amount }
        val avgAmount = if (transactions.isNotEmpty()) totalAmount / transactions.size else 0.0

        return TransactionData(
            id = "AGGREGATED_${System.currentTimeMillis()}",
            date = Clock.System.now(),
            amount = totalAmount,
            currency = transactions.firstOrNull()?.currency ?: "USD",
            type = TransactionType.AGGREGATED,
            counterparty = "MULTIPLE",
            description = "Added ${transactions.size} transactions",
            metadata = mapOf(
                "totalAmount" to totalAmount.toString(),
                "avgAmount" to avgAmount.toString(),
                "count" to transactions.size.toString()
            )
        )
    }

    private fun calculateComplianceRiskScore(
        riskAnalysis: ComplianceRiskAnalysis,
        riskPatterns: List<RiskPattern>,
        regulatoryChanges: List<Any>
    ): Double {
        var score = riskAnalysis.riskScore

        score += riskPatterns.size * 5.0
        score += regulatoryChanges.size * 3.0

        return score.coerceIn(0.0, 100.0)
    }

    private fun generateComplianceRecommendations(
        riskAnalysis: ComplianceRiskAnalysis,
        riskScore: Double,
        industryBenchmarks: IndustryBenchmarks
    ): List<ComplianceRecommendation> {
        val recommendations = riskAnalysis.recommendations.toMutableList()

        if (riskScore > 70) {
            recommendations.add(
                ComplianceRecommendation(
                    code = "AUDIT_REQUIRED",
                    description = "Conduct an immediate internal audit",
                    priority = SeverityStatus.HIGH,
                    estimatedEffort = "2-4 weeks"
                )
            )
        }

        if (riskScore > 50) {
            recommendations.add(
                ComplianceRecommendation(
                    code = "MONTHLY_REVIEW",
                    description = "Review compliance processes monthly",
                    priority = SeverityStatus.MEDIUM,
                    estimatedEffort = "1 day/month"
                )
            )
        }

            if (industryBenchmarks.taxComplianceRate < 0.90) {
                recommendations.add(
                    ComplianceRecommendation(
                        code = "IMPROVE_TAX_COMPLIANCE",
                        description = "Tax compliance rate is below industry average of ${industryBenchmarks.taxComplianceRate}",
                        priority = SeverityStatus.MEDIUM,
                        estimatedEffort = "Variable"
                    )
                )
            }

        return recommendations
    }
}