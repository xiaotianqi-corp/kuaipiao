package org.xiaotianqi.kuaipiao.core.logic.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import org.xiaotianqi.kuaipiao.core.exceptions.AiValidationException
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.invoice.toDocumentData
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
class ResponseValidator {

    fun validateInvoiceResponse(result: InvoiceProcessingResult): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val doc = result.extractedData.toDocumentData()

        // Validar campos obligatorios
        if (doc.invoiceNumber.isNullOrBlank()) {
            errors.add("Número de factura no puede estar vacío")
        }

        if (doc.total == null || doc.total!! <= 0.0) {
            errors.add("Total debe ser mayor a 0")
        }

        // Validar formato de fechas
        if (doc.issueDate == null || !isValidDate(doc.issueDate!!)) {
            errors.add("Formato de fecha inválido: ${doc.issueDate}")
        }

        // Validar confianza
        if (result.confidence < 0.6) {
            warnings.add("Baja confianza en extracción: ${result.confidence}")
        }

        // Validar productos
        if (doc.items.isEmpty()) {
            warnings.add("No se detectaron productos en la factura")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    fun validateTariffClassification(classification: TariffClassification): ValidationResult {
        val errors = mutableListOf<String>()

        if (classification.tariffCode.isBlank()) {
            errors.add("Código arancelario no puede estar vacío")
        }

        if (classification.confidence < 0.3) {
            errors.add("Confianza demasiado baja: ${classification.confidence}")
        }

        if (!isValidTariffCode(classification.tariffCode)) {
            errors.add("Formato de código arancelario inválido: ${classification.tariffCode}")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun validateComplianceAnalysis(analysis: ComplianceRiskAnalysis): ValidationResult {
        val errors = mutableListOf<String>()

        if (analysis.riskScore < 0 || analysis.riskScore > 1) {
            errors.add("Risk score debe estar entre 0 y 1")
        }

        if (analysis.auditProbability < 0 || analysis.auditProbability > 1) {
            errors.add("Audit probability debe estar entre 0 y 1")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    private fun isValidDate(dateStr: String): Boolean {
        return try {
            java.time.LocalDate.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidTariffCode(code: String): Boolean {
        return code.matches(Regex("\\d{4,10}"))
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
) {
    fun throwIfInvalid() {
        if (!isValid) {
            throw AiValidationException(
                "Validación fallida: ${errors.joinToString()}",
                validationErrors = errors
            )
        }
    }
}