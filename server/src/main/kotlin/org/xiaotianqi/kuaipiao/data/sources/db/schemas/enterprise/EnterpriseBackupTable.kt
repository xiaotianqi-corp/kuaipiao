package org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise


import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseBackupData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object EnterpriseBackupsTable : UUIDTable("enterprise_backups") {
    val enterprise = reference("enterprise_id", EnterprisesTable)
    val description = text("description").nullable()
    val backupPath = varchar("backup_path", 255)
    val size = long("size")
    val status = enumerationByName("status", 20, EntityStatus::class)
    val includeData = bool("include_data").default(true)
    val includeSchema = bool("include_schema").default(true)
    val createdAt = timestamp("created_at")
    val completedAt = timestamp("completed_at").nullable()
    val error = text("error").nullable()
}

class EnterpriseBackupEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EnterpriseBackupEntity>(EnterpriseBackupsTable)

    var enterprise by EnterpriseEntity referencedOn EnterpriseBackupsTable.enterprise
    var description by EnterpriseBackupsTable.description
    var backupPath by EnterpriseBackupsTable.backupPath
    var size by EnterpriseBackupsTable.size
    var status by EnterpriseBackupsTable.status
    var includeData by EnterpriseBackupsTable.includeData
    var includeSchema by EnterpriseBackupsTable.includeSchema
    var createdAt by EnterpriseBackupsTable.createdAt
    var completedAt by EnterpriseBackupsTable.completedAt
    var error by EnterpriseBackupsTable.error
}

@ExperimentalTime
fun EnterpriseBackupEntity.fromData(data: EnterpriseBackupData, enterpriseEntity: EnterpriseEntity) {
    enterprise = enterpriseEntity
    description = data.description
    backupPath = data.backupPath
    size = data.size
    status = data.status
    includeData = data.includeData
    includeSchema = data.includeSchema
    createdAt = data.createdAt.toJavaInstant()
    completedAt = data.completedAt?.toJavaInstant()
    error = data.error
}

@ExperimentalTime
fun EnterpriseBackupEntity.toData() = EnterpriseBackupData(
    id = id.value.toString(),
    enterpriseId = enterprise.id.value.toString(),
    description = description,
    backupPath = backupPath,
    size = size,
    status = status,
    includeData = includeData,
    includeSchema = includeSchema,
    createdAt = createdAt.toKotlinInstant(),
    completedAt = completedAt?.toKotlinInstant(),
    error = error,
)
