package org.xiaotianqi.kuaipiao.domain.organization

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class OrganizationData(
    val id: String,
    val userIds: List<String> = emptyList(),
    val name: String,
    val code: String,
    val address: String,
    val phone: String,
    val email: String,
    val country: String,
    val city: String,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val metadata: String?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
)

@Serializable
@ExperimentalTime
data class OrganizationCreateData(
    val id: String,
    val userIds: List<String> = emptyList(),
    val name: String,
    val code: String,
    val address: String,
    val phone: String,
    val email: String,
    val country: String,
    val city: String,
    val metadata: String?,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
)

@Serializable
@ExperimentalTime
data class OrganizationResponse(
    val id: String,
    val userIds: List<String> = emptyList(),
    val name: String,
    val code: String,
    val address: String,
    val phone: String,
    val email: String,
    val country: String,
    val city: String,
    val metadata: String?,
    val status: EntityStatus = EntityStatus.ACTIVE,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
)

@Serializable
data class UpdateStatusRequest(
    val status: EntityStatus
)
