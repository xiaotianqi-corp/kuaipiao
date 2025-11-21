package org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise


import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseData
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object EnterprisesTable : UUIDTable("enterprises") {
    val subdomain = varchar("subdomain", 63).uniqueIndex()
    val domain = varchar("domain", 255).nullable()
    val status = enumerationByName("status", 20, EntityStatus::class)
    val plan = enumerationByName("plan", 20, EnterprisePlan::class)
    val settings = text("settings").nullable()
    val metadata = text("metadata").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    val expiresAt = timestamp("expires_at").nullable()
}

class EnterpriseEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EnterpriseEntity>(EnterprisesTable)
    var subdomain by EnterprisesTable.subdomain
    var domain by EnterprisesTable.domain
    var status by EnterprisesTable.status
    var plan by EnterprisesTable.plan
    var settings by EnterprisesTable.settings
    var metadata by EnterprisesTable.metadata
    var createdAt by EnterprisesTable.createdAt
    var updatedAt by EnterprisesTable.updatedAt
    var expiresAt by EnterprisesTable.expiresAt
}

@ExperimentalTime
fun EnterpriseEntity.fromData(data: EnterpriseData) {
    subdomain = data.subdomain
    domain = data.domain
    status = data.status
    plan = data.plan
    settings = data.settings
    metadata = data.metadata
    createdAt = data.createdAt.toJavaInstant()
    updatedAt = data.updatedAt?.toJavaInstant()
    expiresAt = data.expiresAt?.toJavaInstant()
}

@ExperimentalTime
fun EnterpriseEntity.toData() = EnterpriseData(
    id = id.value.toString(),
    subdomain = subdomain,
    domain = domain,
    status = status,
    plan = plan,
    settings = settings,
    metadata = metadata,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    expiresAt = expiresAt?.toKotlinInstant(),
)