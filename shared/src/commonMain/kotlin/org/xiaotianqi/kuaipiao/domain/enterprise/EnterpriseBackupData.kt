package org.xiaotianqi.kuaipiao.domain.enterprise

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class EnterpriseBackupData(
    val id: String,
    val enterpriseId: String,
    val description: String? = null,
    val backupPath: String,
    val size: Long,
    val status: EntityStatus = EntityStatus.PENDING,
    val includeData: Boolean = true,
    val includeSchema: Boolean = true,
    val createdAt: Instant,
    val completedAt: Instant? = null,
    val error: String? = null,
)

@Serializable
@ExperimentalTime
data class EnterpriseBackupCreateData(
    val enterpriseId: String,
    val description: String? = null,
    val backupPath: String,
    val includeData: Boolean = true,
    val includeSchema: Boolean = true,
    val createdAt: Instant,
)

@Serializable
@ExperimentalTime
data class EnterpriseBackupResponse(
    val id: String,
    val enterpriseId: String,
    val backupPath: String,
    val status: EntityStatus,
    val size: Long,
    val createdAt: Instant,
)
