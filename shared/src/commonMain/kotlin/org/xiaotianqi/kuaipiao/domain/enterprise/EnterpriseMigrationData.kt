package org.xiaotianqi.kuaipiao.domain.enterprise

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.OperationStatus

@Serializable
data class EnterpriseMigrationData(
    val id: String,
    val enterpriseId: String,
    val version: String,
    val description: String,
    val script: String,
    val status: OperationStatus = OperationStatus.PENDING,
    val appliedAt: Long,
    val executionTime: Long,
    val errorMessage: String? = null,
    val checksum: String? = null,
)

@Serializable
data class EnterpriseMigrationCreateData(
    val enterpriseId: String,
    val version: String,
    val description: String,
    val script: String,
)

@Serializable
data class EnterpriseMigrationResponse(
    val id: String,
    val enterpriseId: String,
    val version: String,
    val status: OperationStatus,
    val appliedAt: Long,
)
