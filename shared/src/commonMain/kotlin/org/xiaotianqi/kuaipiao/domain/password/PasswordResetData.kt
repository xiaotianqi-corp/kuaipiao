package org.xiaotianqi.kuaipiao.domain.password

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.utils.DateTimeUtils

@Serializable
data class PasswordResetData(
    val token: String,
    val userId: String,
    val expireAt: Long,
    val createdAt: Long = DateTimeUtils.currentMillis()
)