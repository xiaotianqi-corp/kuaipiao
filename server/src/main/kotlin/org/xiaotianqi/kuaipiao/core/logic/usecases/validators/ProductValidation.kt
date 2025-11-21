package org.xiaotianqi.kuaipiao.core.logic.usecases.validators

import org.xiaotianqi.kuaipiao.domain.predictions.PredictionData

fun validateClassification(pred: PredictionData): String? {

    if (pred.tariffCode == null) return "El modelo no devolvió un journal entry"
    if (pred.confidence < 0.5) return "Low confidence in the ranking"
    if (pred.taxCategory == null) return "The tax category is absent"

    return null
}
