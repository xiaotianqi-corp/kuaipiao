package org.xiaotianqi.kuaipiao.core.logic.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceCheck
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceWarning
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.risk.RiskPattern
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.enums.RiskPatternType
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import org.xiaotianqi.kuaipiao.enums.TaxType
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.daos.benchmark.BenchmarkDao
import org.xiaotianqi.kuaipiao.data.daos.company.CompanyDao
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.trade.ExportRules
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

private val logger = KotlinLogging.logger {}

@Single
@ExperimentalTime
@ExperimentalUuidApi
class TaxComplianceAnalyzer (
    private val companyDao: CompanyDao,
    private val benchmarkDao: BenchmarkDao
) {

    fun validateExportCompliance(
        documentData: InvoiceProcessingResult,
        tariffClassifications: List<TariffClassification>,
        exportRules: ExportRules
    ): ComplianceCheck {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ComplianceWarning>()
        val missingDocuments = mutableListOf<String>()

        exportRules.requiredDocuments.forEach { doc ->
            if (!hasRequiredDocument(documentData, doc)) {
                missingDocuments.add(doc)
            }
        }

        tariffClassifications.forEach { classification ->
            if (classification.confidence < 0.7) {
                warnings.add(ComplianceWarning(
                    code = "LOW_CONFIDENCE_TARIFF",
                    message = "Low confidence in ranking: ${classification.productDescription}",
                    severity = SeverityStatus.MEDIUM
                ))
            }

            if (classification.restrictions?.isRestricted == true) {
                errors.add(ValidationError(
                    code = "RESTRICTED_PRODUCT",
                    field = "tariffCode",
                    message = "Restricted product: ${classification.tariffCode}",
                    severity = listOf(SeverityStatus.HIGH)
                ))
            }
        }

        val calculationErrors = validateTaxCalculations(documentData.extractedData)
        errors.addAll(calculationErrors)

        if (!isWithinValidDateRange(documentData.extractedData.date)) {
            warnings.add(ComplianceWarning(
                code = "DATE_VALIDITY",
                message = "Issue date outside the valid period",
                severity = SeverityStatus.LOW
            ))
        }

        val riskScore = calculateRiskScore(errors, warnings, missingDocuments)

        val issues = buildList {
            errors.forEach { add("ERROR: ${it.message}") }
            warnings.forEach { add("WARNING: ${it.message}") }
            missingDocuments.forEach { add("MISSING: $it") }
        }

        return ComplianceCheck(
            isCompliant = errors.isEmpty() && missingDocuments.isEmpty(),
            riskScore = riskScore,
            issues = issues.takeIf { it.isNotEmpty() },
            missingDocuments = missingDocuments,
            validationErrors = errors,
            warnings = warnings,
            suggestedActions = generateSuggestedActions(errors, warnings, missingDocuments)
        )
    }

    private fun calculateRiskScore(
        errors: List<ValidationError>,
        warnings: List<ComplianceWarning>,
        missingDocuments: List<String>
    ): Double {
        var score = 0.0

        score += errors.count { it.severity.contains(SeverityStatus.HIGH) } * 0.3
        score += errors.count { it.severity.contains(SeverityStatus.MEDIUM) } * 0.2
        score += errors.count { it.severity.contains(SeverityStatus.LOW) } * 0.1

        score += warnings.size * 0.1

        score += missingDocuments.size * 0.15

        return score.coerceIn(0.0, 1.0)
    }

    fun analyzeTransactionPatterns(transactions: List<TransactionData>): List<RiskPattern> {
        val patterns = mutableListOf<RiskPattern>()

        val roundAmounts = transactions.filter { isRoundAmount(it.amount) }
        if (roundAmounts.size > transactions.size * 0.1) {
            patterns.add(RiskPattern(
                patternType = RiskPatternType.ROUND_AMOUNTS,
                description = "${roundAmounts.size} transactions with round amounts",
                severity = SeverityStatus.MEDIUM,
                occurrences = roundAmounts.size,
                totalAmount = roundAmounts.sumOf { it.amount },
                firstOccurrence = roundAmounts.minByOrNull { it.date }?.date ?: Clock.System.now(),
                lastOccurrence = roundAmounts.maxByOrNull { it.date }?.date ?: Clock.System.now()
            ))
        }

        val afterHours = transactions.filter { isAfterHours(it.date) }
        if (afterHours.isNotEmpty()) {
            patterns.add(RiskPattern(
                patternType = RiskPatternType.AFTER_HOURS,
                description = "${afterHours.size} after-hours transactions",
                severity = SeverityStatus.LOW,
                occurrences = afterHours.size,
                totalAmount = afterHours.sumOf { it.amount },
                firstOccurrence = afterHours.minByOrNull { it.date }?.date ?: Clock.System.now(),
                lastOccurrence = afterHours.maxByOrNull { it.date }?.date ?: Clock.System.now()
            ))
        }

        val smallFrequent = detectSmallFrequentTransactions(transactions)
        if (smallFrequent.isNotEmpty()) {
            patterns.add(RiskPattern(
                patternType = RiskPatternType.FREQUENT_SMALL_TRANSACTIONS,
                description = "${smallFrequent.size} frequent small transactions",
                severity = SeverityStatus.HIGH,
                occurrences = smallFrequent.size,
                totalAmount = smallFrequent.sumOf { it.amount },
                firstOccurrence = smallFrequent.minByOrNull { it.date }?.date ?: Clock.System.now(),
                lastOccurrence = smallFrequent.maxByOrNull { it.date }?.date ?: Clock.System.now()
            ))
        }

        return patterns
    }

    suspend fun analyzeHistoricalRiskPatterns(
        transactions: List<TransactionData>,
        range: DateRange
    ): List<RiskPattern> {

        val filtered = transactions.filter {
            it.date >= range.start && it.date <= range.end
        }

        if (filtered.isEmpty()) return emptyList()

        val basePatterns = analyzeTransactionPatterns(filtered).toMutableList()

        val sortedByDate = filtered.sortedBy { it.date }
        val firstHalf = sortedByDate.take(sortedByDate.size / 2)
        val secondHalf = sortedByDate.takeLast(sortedByDate.size / 2)

        if (firstHalf.isNotEmpty() && secondHalf.isNotEmpty()) {
            val avg1 = firstHalf.map { it.amount }.average()
            val avg2 = secondHalf.map { it.amount }.average()

            if (avg2 > avg1 * 1.5) {
                basePatterns.add(
                    RiskPattern(
                        patternType = RiskPatternType.SUDDEN_INCREASE,
                        description = "Notable increment in transaction volume/amount",
                        severity = SeverityStatus.MEDIUM,
                        occurrences = filtered.size,
                        totalAmount = filtered.sumOf { it.amount },
                        firstOccurrence = filtered.first().date,
                        lastOccurrence = filtered.last().date
                    )
                )
            }
        }

        return basePatterns
    }


    suspend fun getIndustryBenchmarks(companyId: String): IndustryBenchmarks {
        return withContext(Dispatchers.IO) {
            try {
                val company = companyDao.getById(companyId)
                    ?: return@withContext getDefaultBenchmarks()

                val industry = company.address.country
                benchmarkDao.findByIndustry(industry)
                    ?: getDefaultBenchmarks()
            } catch (e: Exception) {
                logger.error(e) { "Error fetching benchmarks for company $companyId" }
                getDefaultBenchmarks()
            }
        }
    }

    private fun getDefaultBenchmarks(): IndustryBenchmarks {
        val allRiskPatterns = enumValues<RiskPatternType>().toList()
        return IndustryBenchmarks(
            industry = "General",
            avgTransactionSize = 3000.0,
            typicalTransactionCount = 80,
            commonRiskPatterns = allRiskPatterns
        )
    }

    private fun hasRequiredDocument(documentData: InvoiceProcessingResult, document: String): Boolean {
        return documentData.extractedData.items.isNotEmpty()
    }

    private fun validateTaxCalculations(invoiceData: InvoiceData): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        val subtotal = invoiceData.subtotal.toDoubleOrNull() ?: 0.0
        val tax = invoiceData.tax.toDoubleOrNull() ?: 0.0
        val total = invoiceData.total.toDoubleOrNull() ?: 0.0

        val calculatedTotal = subtotal + tax
        if (abs(calculatedTotal - total) > 0.01) {
            errors.add(ValidationError(
                code = "TOTAL_MISMATCH",
                field = "total",
                message = "Discrepancy in total calculation: calculated=$calculatedTotal Total, declared=$total",
                severity = listOf(SeverityStatus.HIGH)
            ))
        }

        val iva = invoiceData.taxes.find { it.type == TaxType.IVA }
        iva?.let {
            val expectedIva = subtotal * 0.15
            if (abs(tax - expectedIva) > 0.01) {
                errors.add(ValidationError(
                    code = "IVA",
                    field = "iva",
                    message = "Incorrect VAT: expected=$expectedIva, calculated=$tax",
                    severity = listOf(SeverityStatus.HIGH)
                ))
            }
        }

        return errors
    }

    private fun isWithinValidDateRange(date: Instant): Boolean {
        return try {
            val today = Clock.System.now()
            val oneYearAgo = today - 365.days
            date in oneYearAgo..today
        } catch (e: Exception) {
            false
        }
    }

    private fun isRoundAmount(amount: Double): Boolean {
        return amount % 100 == 0.0
    }

    private fun isAfterHours(date: Instant): Boolean {
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = date.toLocalDateTime(timeZone)
        val hour = localDateTime.hour
        return hour !in 8..18
    }

    private fun detectSmallFrequentTransactions(transactions: List<TransactionData>): List<TransactionData> {
        val smallTransactions = transactions.filter { it.amount < 100 }
        val groupedByDay = smallTransactions.groupBy {
            it.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

        return groupedByDay.filter { it.value.size > 5 }
            .flatMap { it.value }
    }

    private fun generateSuggestedActions(
        errors: List<ValidationError>,
        warnings: List<ComplianceWarning>,
        missingDocs: List<String>
    ): List<String> {
        val actions = mutableListOf<String>()

        if (missingDocs.isNotEmpty()) {
            actions.add("Upload missing documents: ${missingDocs.joinToString()}")
        }

        if (errors.any { it.severity.contains(SeverityStatus.HIGH) }) {
            actions.add("Review and correct validation errors before proceeding")
        }

        if (warnings.isNotEmpty()) {
            actions.add("Review compliance warnings")
        }

        return actions
    }
}