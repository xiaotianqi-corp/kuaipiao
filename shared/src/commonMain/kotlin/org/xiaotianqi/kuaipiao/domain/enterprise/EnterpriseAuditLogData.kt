package org.xiaotianqi.kuaipiao.domain.enterprise

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class EnterpriseAuditLogData(
    val id: String,
    val enterpriseId: String,
    val action: String,
    val actorId: String,
    val actorType: String,
    val details: String,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val createdAt: Instant,
)

@Serializable
@ExperimentalTime
data class EnterpriseAuditLogCreateData(
    val enterpriseId: String,
    val action: String,
    val actorId: String,
    val actorType: String,
    val details: String,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val createdAt: Instant = Clock.System.now(),
)

@Serializable
@ExperimentalTime
data class EnterpriseAuditLogResponse(
    val id: String,
    val enterpriseId: String,
    val action: String,
    val actorId: String,
    val actorType: String,
    val createdAt: Instant = Clock.System.now(),
)
