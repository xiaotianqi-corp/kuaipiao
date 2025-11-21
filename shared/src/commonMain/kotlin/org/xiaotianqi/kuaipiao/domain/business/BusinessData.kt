package org.xiaotianqi.kuaipiao.domain.business

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.domain.validation.ValidationWarning

@Serializable
data class BusinessRulesValidation(
    val isValid: Boolean,
    val errors: List<ValidationError>,
    val warnings: List<ValidationWarning>,
    val suggestions: List<String>
)
