package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserResponse
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.SizedCollection
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import java.time.ZoneOffset
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@ExperimentalTime
fun UserEntity.toDomain() = UserData(
    id = id.value.toString(),
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    emailVerified = emailVerified,
    passwordHash = passwordHash,
    enterpriseId = enterprise.toString(),
    organizationIds = organizations.map { it.id.value.toString() },
    isActive = isActive,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    lastLoginAt = lastLoginAt?.toKotlinInstant(),
)

@ExperimentalTime
fun UserEntity.toResponse() = UserResponse(
    id = id.value.toString(),
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    enterpriseId = enterprise.toString(),
    organizationIds = organizations.map { it.id.value.toString() },
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
)

@ExperimentalTime
fun UserData.toResponse() = UserResponse(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    enterpriseId = enterpriseId,
    organizationIds = organizationIds,
    createdAt = createdAt,
)

@ExperimentalTime
fun UserEntity.fromCreateData(
    data: UserCreateData,
    enterpriseEntity: EnterpriseEntity,
    organizationEntities: List<OrganizationEntity> = emptyList()
) {
    username = data.username
    firstName = data.firstName
    lastName = data.lastName
    enterprise = enterpriseEntity
    organizations = SizedCollection(organizationEntities)
    email = data.email
    passwordHash = data.passwordHash
    createdAt = data.createdAt.toJavaInstant()
}
