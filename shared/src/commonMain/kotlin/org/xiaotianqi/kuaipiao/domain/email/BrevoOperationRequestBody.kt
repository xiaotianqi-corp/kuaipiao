package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable

@Serializable
data class BrevoOperationRequestBody(
    val to: List<BrevoEmailField>,
    val templateId: Long
)