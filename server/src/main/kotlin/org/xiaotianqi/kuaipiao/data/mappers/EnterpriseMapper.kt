package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseResponse
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@ExperimentalTime
fun EnterpriseEntity.toDomain() = EnterpriseData(
    id = id.value.toString(),
    domain = domain,
    subdomain = subdomain,
    status = status,
    plan = plan,
    settings = settings,
    metadata = metadata,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    expiresAt = expiresAt?.toKotlinInstant(),
)

@ExperimentalTime
fun EnterpriseEntity.toResponse() = EnterpriseResponse(
    id = id.value.toString(),
    domain = domain,
    subdomain = subdomain,
    status = status,
    plan = plan,
    settings = settings,
    metadata = metadata,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    expiresAt = expiresAt?.toKotlinInstant(),
)

@ExperimentalTime
fun EnterpriseData.toResponse() = EnterpriseResponse(
    id = id,
    domain = domain,
    subdomain = subdomain,
    status = status,
    plan = plan,
    settings = settings,
    metadata = metadata,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = expiresAt
)

@ExperimentalTime
fun EnterpriseEntity.fromCreateData(data: EnterpriseCreateData) {
    domain = data.domain
    subdomain = data.subdomain
    status = data.status
    plan = data.plan
    settings = data.settings
    metadata = data.metadata
    createdAt = data.createdAt.toJavaInstant()
    updatedAt = data.updatedAt?.toJavaInstant()
}
