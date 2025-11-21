package org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseAuditLogData
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object EnterpriseAuditLogTable : UUIDTable("enterprise_audit_logs") {
    val enterprise= reference("_id", EnterprisesTable)
    val action = varchar("action", 50)
    val actorId = varchar("actor_id", 255)
    val actorType = varchar("actor_type", 50)
    val details = text("details")
    val ipAddress = varchar("ip_address", 45).nullable()
    val userAgent = text("user_agent").nullable()
    val createdAt = timestamp("created_at")
}

class EnterpriseAuditLogEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EnterpriseAuditLogEntity>(EnterpriseAuditLogTable)

    var enterprise by EnterpriseEntity referencedOn EnterpriseAuditLogTable.enterprise
    var action by EnterpriseAuditLogTable.action
    var actorId by EnterpriseAuditLogTable.actorId
    var actorType by EnterpriseAuditLogTable.actorType
    var details by EnterpriseAuditLogTable.details
    var ipAddress by EnterpriseAuditLogTable.ipAddress
    var userAgent by EnterpriseAuditLogTable.userAgent
    var createdAt by EnterpriseAuditLogTable.createdAt
}

@ExperimentalTime
fun EnterpriseAuditLogEntity.fromData(logData: EnterpriseAuditLogData, enterpriseEntity: EnterpriseEntity) {
    enterprise = enterpriseEntity
    action = logData.action
    actorId = logData.actorId
    actorType = logData.actorType
    details = logData.details
    ipAddress = logData.ipAddress
    userAgent = logData.userAgent
    createdAt = logData.createdAt.toJavaInstant()
}

@ExperimentalTime
fun EnterpriseAuditLogEntity.toData() = EnterpriseAuditLogData(
    id = UUID.randomUUID().toString(),
    enterpriseId = enterprise.toString(),
    action = action,
    actorId = actorId,
    actorType = actorType,
    details = details,
    ipAddress = ipAddress,
    userAgent = userAgent,
    createdAt = createdAt.toKotlinInstant()
)
