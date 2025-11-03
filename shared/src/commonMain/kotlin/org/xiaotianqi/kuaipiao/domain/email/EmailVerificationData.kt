package org.xiaotianqi.kuaipiao.domain.email

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.utils.DateTimeUtils

@Serializable
data class EmailVerificationData(
    val token: String,
    val userId: String,
    val expireAt: Long,
    val createdAt: Long = DateTimeUtils.currentMillis()
)