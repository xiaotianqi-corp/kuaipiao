package org.xiaotianqi.kuaipiao.core.heuristics

import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceData
import org.xiaotianqi.kuaipiao.domain.validation.DataSuggestion
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import org.xiaotianqi.kuaipiao.utils.formatDouble
import kotlin.math.abs
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun applyAllHeuristics(
    invoice: InvoiceData,
    country: String
): Triple<Double, List<ValidationError>, List<DataSuggestion>> {

    val allErrors = mutableListOf<ValidationError>()
    val allSuggestions = mutableListOf<DataSuggestion>()

    val (dupErrors, dupSuggestions) = detectDuplicateItems(invoice)
    val (itemErrors, itemSuggestions) = validateItemTotals(invoice)
    val (globalErrors, globalSuggestions) = validateGlobalTotals(invoice)
    val (completenessErrors, completenessSuggestions) = applyCompletenessHeuristic(invoice)

    allErrors += dupErrors
    allErrors += itemErrors
    allErrors += globalErrors
    allErrors += completenessErrors

    allSuggestions += dupSuggestions
    allSuggestions += itemSuggestions
    allSuggestions += globalSuggestions
    allSuggestions += completenessSuggestions

    val confidence = calculateConfidence(invoice, allErrors, country)

    return Triple(confidence, allErrors, allSuggestions)
}

private fun normalizeNumber(value: String?): Double {
    if (value.isNullOrBlank()) return 0.0
    return value
        .replace(",", ".")
        .replace(" ", "")
        .replace("$", "")
        .toDoubleOrNull() ?: 0.0
}

@ExperimentalTime
private fun detectDuplicateItems(invoice: InvoiceData):
        Pair<List<ValidationError>, List<DataSuggestion>> {

    val errors = mutableListOf<ValidationError>()
    val suggestions = mutableListOf<DataSuggestion>()

    val duplicates = invoice.items
        .groupBy { it.description.trim().lowercase() }
        .filter { it.value.size > 1 }

    duplicates.forEach { (desc, items) ->
        errors.add(
            ValidationError(
                code = "DUPLICATE_ITEM",
                message = "El ítem \"$desc\" aparece duplicado.",
                field = "items",
                severity = listOf(SeverityStatus.MEDIUM)
            )
        )

        suggestions.add(
            DataSuggestion(
                fieldName = "items",
                originalValue = items.joinToString { it.id },
                suggestedValue = "Consolidar ítems duplicados",
                reason = "OCR puede duplicar líneas con textos similares",
                confidence = 0.60
            )
        )
    }

    return errors to suggestions
}

@ExperimentalTime
private fun validateItemTotals(invoice: InvoiceData):
        Pair<List<ValidationError>, List<DataSuggestion>> {

    val errors = mutableListOf<ValidationError>()
    val suggestions = mutableListOf<DataSuggestion>()

    invoice.items.forEach { item ->
        val qty = normalizeNumber(item.quantity)
        val price = normalizeNumber(item.unitPrice)
        val expected = qty * price
        val reported = normalizeNumber(item.total)

        if (abs(expected - reported) > 0.01) {
            errors.add(
                ValidationError(
                    code = "ITEM_TOTAL_MISMATCH",
                    message = "El total del ítem no coincide con cantidad * precio.",
                    field = "items.${item.id}",
                    severity = listOf(SeverityStatus.LOW)
                )
            )

            suggestions.add(
                DataSuggestion(
                    fieldName = "items.${item.id}.total",
                    originalValue = item.total,
                    suggestedValue = formatDouble(expected),
                    reason = "Corrección basada en qty * unitPrice",
                    confidence = 0.75
                )
            )
        }
    }

    return errors to suggestions
}

@ExperimentalTime
private fun validateGlobalTotals(invoice: InvoiceData):
        Pair<List<ValidationError>, List<DataSuggestion>> {

    val errors = mutableListOf<ValidationError>()
    val suggestions = mutableListOf<DataSuggestion>()

    val subtotal = normalizeNumber(invoice.subtotal)
    val tax = normalizeNumber(invoice.tax)
    val total = normalizeNumber(invoice.total)

    if (abs((subtotal + tax) - total) > 0.01) {
        errors.add(
            ValidationError(
                code = "TOTAL_MISMATCH",
                message = "subtotal + tax no coincide con total",
                field = "total",
                severity = listOf(SeverityStatus.HIGH)
            )
        )

        suggestions.add(
            DataSuggestion(
                fieldName = "total",
                originalValue = invoice.total,
                suggestedValue = formatDouble(subtotal + tax),
                reason = "Reconciliación matemática",
                confidence = 0.90
            )
        )
    }

    return errors to suggestions
}

@ExperimentalTime
private fun applyCompletenessHeuristic(invoice: InvoiceData):
        Pair<List<ValidationError>, List<DataSuggestion>> {

    val errors = mutableListOf<ValidationError>()
    val suggestions = mutableListOf<DataSuggestion>()

    val critical = mapOf(
        "number" to invoice.number,
        "providerId.name" to invoice.providerId.name,
        "currency" to invoice.currency,
        "total" to invoice.total
    )

    critical.forEach { (field, value) ->
        if (value.isBlank()) {
            errors.add(
                ValidationError(
                    code = "MISSING_FIELD",
                    message = "Campo requerido vacío: $field",
                    field = field,
                    severity = listOf(SeverityStatus.HIGH)
                )
            )

            suggestions.add(
                DataSuggestion(
                    fieldName = field,
                    originalValue = value,
                    suggestedValue = "N/A",
                    reason = "Campo requerido no fue extraído",
                    confidence = 0.40
                )
            )
        }
    }

    return errors to suggestions
}

private fun countryWeight(country: String): Double = when (country.lowercase()) {
    "ecuador" -> 1.0
    "chile" -> 0.95
    "peru" -> 0.90
    "estados unidos", "usa" -> 0.85
    else -> 0.80
}

@ExperimentalTime
private fun calculateConfidence(
    invoice: InvoiceData,
    errors: List<ValidationError>,
    country: String
): Double {

    var score = 1.0

    val criticalFields = listOf(
        invoice.number,
        invoice.providerId.name,
        invoice.currency,
        invoice.total
    )

    val emptyCritical = criticalFields.count { it.isBlank() }
    score -= emptyCritical * 0.15

    if (invoice.items.isEmpty()) score -= 0.20

    score -= errors.size * 0.08

    score *= countryWeight(country)

    return score.coerceIn(0.0, 1.0)
}
