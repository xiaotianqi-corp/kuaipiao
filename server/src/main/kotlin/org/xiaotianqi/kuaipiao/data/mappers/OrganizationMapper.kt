package org.xiaotianqi.kuaipiao.data.mappers

import org.jetbrains.exposed.sql.SizedCollection
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationResponse
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@ExperimentalTime
fun OrganizationEntity.toDomain() = OrganizationData(
    id = id.value.toString(),
    name = name,
    code = code,
    address = address,
    phone = phone,
    email = email,
    country = country,
    city = city,
    status = status,
    metadata = metadata,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    userIds = users.map { it.id.value.toString() }
)

@ExperimentalTime
fun OrganizationEntity.toResponse() = OrganizationResponse(
    id = id.value.toString(),
    name = name,
    code = code,
    address = address,
    phone = phone,
    email = email,
    country = country,
    city = city,
    status = status,
    metadata = metadata,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    userIds = users.map { it.id.value.toString() }
)

@ExperimentalTime
fun OrganizationData.toResponse() = OrganizationResponse(
    id = id,
    userIds = userIds,
    name = name,
    code = code,
    address = address,
    phone = phone,
    email = email,
    country = country,
    city = city,
    status = status,
    metadata = metadata,
    createdAt = createdAt,
    updatedAt = updatedAt
)

@ExperimentalTime
fun OrganizationEntity.fromCreateData(
    data: OrganizationCreateData,
    userEntities: List<UserEntity> = emptyList()
    ) {
    users = SizedCollection(userEntities)
    name = data.name
    code = data.code
    address = data.address
    phone = data.phone
    email = data.email
    country = data.country
    city = data.city
    status = data.status
    metadata = data.metadata
    createdAt = data.createdAt.toJavaInstant()
    updatedAt = data.updatedAt?.toJavaInstant()
}
