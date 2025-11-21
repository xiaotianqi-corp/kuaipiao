package org.xiaotianqi.kuaipiao.domain.validation

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.CrossValidationStatus
import org.xiaotianqi.kuaipiao.enums.SeverityStatus

@Serializable
data class ValidationError(
    val code: String,
    val message: String,
    val field: String? = null,
    val severity: List<SeverityStatus> = listOf(SeverityStatus.HIGH)
)

@Serializable
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList(),
    val warnings: List<ValidationWarning> = emptyList()
) {
    fun hasWarnings(): Boolean = warnings.isNotEmpty()
    fun hasErrors(): Boolean = errors.isNotEmpty()

    companion object {
        val VALID = ValidationResult(
            isValid = true,
            errors = emptyList(),
            warnings = emptyList()
        )

        val WARNING = ValidationResult(
            isValid = false,
            errors = emptyList(),
            warnings = listOf(
                ValidationWarning(
                    code = "WARN",
                    message = "Validation contains warnings",
                    severity = listOf(org.xiaotianqi.kuaipiao.enums.SeverityStatus.MEDIUM)
                )
            )
        )
    }
}


@Serializable
data class ValidationWarning(
    val code: String,
    val message: String,
    val severity: List<SeverityStatus> = listOf(SeverityStatus.HIGH)
)

@Serializable
data class ValidationRule(
    val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean = true,
    val severity: List<SeverityStatus> = listOf(SeverityStatus.HIGH),
    val criteria: String
)

@Serializable
data class CrossValidationResult(
    val matchStatus: CrossValidationStatus,
    val discrepancies: List<DataDiscrepancy>,
    val deviations: List<DataDiscrepancy>,
    val matchRate: Double,
    val overallValidationResult: ValidationResult,
    val confidenceScore: Double
)

@Serializable
data class DataDiscrepancy(
    val field: String,
    val sourceAValue: String? = null,
    val sourceBValue: String? = null,
    val message: String,
    val severity: SeverityStatus
)

@Serializable
data class DataSuggestion(
    val fieldName: String,
    val suggestedValue: String,
    val originalValue: String,
    val reason: String? = null,
    val confidence: Double
)