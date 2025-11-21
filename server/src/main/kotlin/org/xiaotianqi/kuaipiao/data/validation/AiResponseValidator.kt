package org.xiaotianqi.kuaipiao.data.validation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.core.exceptions.AiValidationException
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingEntry
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import org.xiaotianqi.kuaipiao.domain.document.OcrResult
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.domain.validation.ValidationResult
import org.xiaotianqi.kuaipiao.domain.validation.ValidationWarning
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class AiResponseValidator {

    fun validateInvoiceData(invoiceData: InvoiceData): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        if (invoiceData.number.isBlank()) {
            errors.add(
                ValidationError(
                    code = "INVOICE_NUMBER_EMPTY",
                    message = "Invoice number cannot be empty"
                )
            )
        }

        if (invoiceData.date.toString().isBlank()) {
            errors.add(
                ValidationError(
                    code = "ISSUE_DATE_EMPTY",
                    message = "Invoice number cannot be empty"
                )
            )
        }

        invoiceData.providerId.taxId?.let { ruc ->
            validateRuc(ruc, "emitter")?.let { errors.add(it) }
        } ?: errors.add(
            ValidationError(
                code = "EMITTER_RUC_MISSING",
                message = "Provider RUC (taxId) is required"
            )
        )
        if (invoiceData.customerId.id.isBlank()) {
            warnings.add(
                ValidationWarning(
                    code = "CUSTOMER_ID_EMPTY",
                    message = "Customer ID is missing",
                    severity = listOf(SeverityStatus.MEDIUM)
                )
            )
        }

        validateAmounts(invoiceData)?.let { amountErrors ->
            errors.addAll(amountErrors)
        }

        if (invoiceData.items.isEmpty()) {
            warnings.add(
                ValidationWarning(
                    code = "NO_ITEMS",
                    message = "Invoice does not contain products",
                    severity = listOf(SeverityStatus.MEDIUM)
                )
            )
        }

        validateCalculations(invoiceData)?.let { calcErrors ->
            errors.addAll(calcErrors)
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun validateTariffClassification(classification: TariffClassification): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        if (classification.tariffCode.isBlank()) {
            errors.add(
                ValidationError(
                    code = "TARIFF_CODE_EMPTY",
                    message = "The tariff code cannot be empty."
                )
            )
        }



        if (classification.confidence < 0.3) {
            errors.add(
                ValidationError(
                    code = "LOW_CONFIDENCE",
                    message = "Confidence too low for classification"
                )
            )
        } else if (classification.confidence < 0.7) {
            warnings.add(
                ValidationWarning(
                    code = "MEDIUM_CONFIDENCE",
                    message = "Average confidence in classification",
                    severity = listOf(SeverityStatus.MEDIUM)
                )
            )
        }

        if (!isValidTariffCodeFormat(classification.tariffCode)) {
            errors.add(
                ValidationError(
                    code = "INVALID_TARIFF_FORMAT",
                    message = "Invalid tariff code format"
                )
            )
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun validateComplianceAnalysis(analysis: ComplianceRiskAnalysis): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        if (analysis.riskScore !in 0.0..1.0) {
            errors.add(
                ValidationError(
                    code =  "INVALID_RISK_SCORE",
                    message = "Risk score must be between 0 and 1"
                )
            )
        }

        if (analysis.auditProbability !in 0.0..1.0) {
            errors.add(
                ValidationError(
                    code = "INVALID_AUDIT_PROBABILITY",
                    message = "Audit probability must be between 0 and 1"
                )
            )
        }

        if (analysis.recommendations.isEmpty() && analysis.riskScore > 0.7) {
            warnings.add(
                ValidationWarning(
                    code = "NO_RECOMMENDATIONS",
                    message = "High-risk analysis without recommendations",
                    severity = listOf(SeverityStatus.HIGH)
                )
            )
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun validateAccountingReconciliation(reconciliation: AccountingReconciliationResult): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        if (reconciliation.suggestedEntries.isEmpty()) {
            errors.add(
                ValidationError(
                    code = "NO_ACCOUNTING_ENTRIES",
                    message = "No accounting entries were generated"
                )
            )
        }

        if (reconciliation.confidenceScore < 0.3) {
            errors.add(
                ValidationError(
                    code = "LOW_CONFIDENCE_RECONCILIATION",
                    message = "Trust too low for reconciliation"
                )
            )
        }

        validateAccountingBalance(reconciliation.suggestedEntries)?.let { balanceError ->
            errors.add(balanceError)
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun validateOcrResult(ocrResult: OcrResult): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        if (ocrResult.extractedText.isBlank()) {
            errors.add(
                ValidationError(
                    code = "NO_TEXT_EXTRACTED",
                    message = "Text could not be extracted from the document"
                )
            )
        }

        if (ocrResult.confidence < 0.5) {
            warnings.add(
                ValidationWarning(
                    code = "LOW_OCR_CONFIDENCE",
                    message = "Low confidence in OCR",
                    severity = listOf(SeverityStatus.MEDIUM)
                )
            )
        }

        if (ocrResult.pages.isEmpty()) {
            warnings.add(
                ValidationWarning(
                    code = "NO_PAGES_DETECTED",
                    message = "No pages were detected in the document.",
                    severity = listOf(SeverityStatus.LOW)
                )
            )
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    private fun validateRuc(ruc: String, type: String): ValidationError? {
        if (ruc.isBlank()) return null

        return when {
            ruc.length !in 10..13 -> ValidationError(
                code = "INVALID_RUC_LENGTH",
                message = "RUC of $type has invalid length: ${ruc.length}"
            )
            !ruc.all { it.isDigit() } -> ValidationError(
                code = "INVALID_RUC_FORMAT",
                message = "RUC of $type contains non-numeric characters"
            )
            else -> null
        }
    }

    private fun validateAmounts(invoiceData: InvoiceData): List<ValidationError>? {
        val errors = mutableListOf<ValidationError>()

        val subtotal = invoiceData.subtotal.toDoubleOrNull()
        val tax = invoiceData.tax.toDoubleOrNull()
        val total = invoiceData.total.toDoubleOrNull()

        if (subtotal == null || subtotal < 0) {
            errors.add(
                ValidationError(
                    code = "INVALID_SUBTOTAL",
                    message = "Subtotal must be greater than 0"
                )
            )
        }

        if (tax == null || tax < 0) {
            errors.add(
                ValidationError(
                    code = "INVALID_TOTAL",
                    message = "Total must be greater than 0"
                )
            )
        }

        if (invoiceData.total < invoiceData.subtotal) {
            errors.add(
                ValidationError(
                    code = "TOTAL_LESS_THAN_SUBTOTAL",
                    message = "The total cannot be less than the subtotal."
                )
            )
        }

        return errors.ifEmpty { null }
    }

    private fun validateCalculations(invoice: InvoiceData): List<ValidationError>? {
        val errors = mutableListOf<ValidationError>()

        val itemsTotal = invoice.items.sumOf { item ->
            val qty = item.quantity.toDoubleOrNull() ?: 0.0
            val price = item.unitPrice.toDoubleOrNull() ?: 0.0
            qty * price
        }

        val subtotalDouble = invoice.subtotal.toDoubleOrNull() ?: 0.0

        if (abs(itemsTotal - subtotalDouble) > 0.01) {
            errors.add(
                ValidationError(
                    code = "SUBTOTAL_MISMATCH",
                    message = "Subtotal does not match the sum of items"
                )
            )
        }

        val taxesTotal = invoice.taxes.sumOf { tax ->
            tax.amount
        }

        val totalDouble = invoice.total.toDoubleOrNull() ?: 0.0

        val calculatedTotal = subtotalDouble + taxesTotal

        if (abs(calculatedTotal - totalDouble) > 0.01) {
            errors.add(
                ValidationError(
                    code = "TOTAL_MISMATCH",
                    message = "Total does not match subtotal + taxes"
                )
            )
        }

        return errors.ifEmpty { null }
    }


    private fun isValidTariffCodeFormat(code: String): Boolean {
        return code.matches(Regex("\\d{4,10}"))
    }

    private fun validateAccountingBalance(entries: List<AccountingEntry>): ValidationError? {
        val debitTotal = entries.sumOf { it.debit }
        val creditTotal = entries.sumOf { it.credit }

        return if (abs(debitTotal - creditTotal) > 0.01) {
            ValidationError(
                code = "UNBALANCED_ENTRIES",
                message = "Accounting entries are not balanced: debit=$debitTotal, credit=$creditTotal"
            )
        } else {
            null
        }
    }


    fun throwIfInvalid(validationResult: ValidationResult, context: String = "") {
        if (!validationResult.isValid) {
            val errorMessages = validationResult.errors.joinToString { it.message }
            throw AiValidationException(
                message = "Validation failed ${if (context.isNotBlank()) " in $context" else ""}: $errorMessages",
                validationErrors = validationResult.errors.map { it.message }
            )
        }
    }
}