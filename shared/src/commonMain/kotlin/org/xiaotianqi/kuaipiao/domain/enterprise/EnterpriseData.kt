package org.xiaotianqi.kuaipiao.domain.enterprise

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class EnterpriseData(
    val id: String,
    val subdomain: String,
    val domain: String? = null,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val plan: EnterprisePlan = EnterprisePlan.FREE,
    val settings: String?,
    val metadata: String?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
    val expiresAt: Instant? = null
)

@Serializable
@ExperimentalTime
data class EnterpriseCreateData(
    val id: String,
    val domain: String? = null,
    val subdomain : String,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val plan: EnterprisePlan = EnterprisePlan.FREE,
    val settings: String,
    val metadata: String,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
    val expiresAt: Instant? = null,
)

@Serializable
@ExperimentalTime
data class EnterpriseResponse(
    val id: String,
    val subdomain: String,
    val domain: String? = null,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val plan: EnterprisePlan = EnterprisePlan.FREE,
    val settings: String?,
    val metadata: String?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
    val expiresAt: Instant? = null,
)
