package org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseMigrationData
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import java.time.Instant
import java.util.*

object EnterpriseMigrationsTable : UUIDTable("enterprise_migrations") {
    val enterprise = reference("enterprise_id", EnterprisesTable)
    val version = varchar("version", 50)
    val description = text("description")
    val script = text("script")
    val status = enumerationByName("status", 20, OperationStatus::class)
    val appliedAt = timestamp("applied_at")
    val executionTime = long("execution_time")
    val errorMessage = text("error_message").nullable()
    val checksum = varchar("checksum", 64).nullable()
}

class EnterpriseMigrationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EnterpriseMigrationEntity>(EnterpriseMigrationsTable)

    var enterprise by EnterpriseEntity referencedOn EnterpriseMigrationsTable.enterprise
    var version by EnterpriseMigrationsTable.version
    var description by EnterpriseMigrationsTable.description
    var script by EnterpriseMigrationsTable.script
    var status by EnterpriseMigrationsTable.status
    var appliedAt by EnterpriseMigrationsTable.appliedAt
    var executionTime by EnterpriseMigrationsTable.executionTime
    var errorMessage by EnterpriseMigrationsTable.errorMessage
    var checksum by EnterpriseMigrationsTable.checksum
}

fun EnterpriseMigrationEntity.fromData(data: EnterpriseMigrationData, enterpriseEntity: EnterpriseEntity) {
    enterprise = enterpriseEntity
    version = data.version
    description = data.description
    script = data.script
    status = data.status
    appliedAt = Instant.ofEpochMilli(data.appliedAt)
    executionTime = data.executionTime
    errorMessage = data.errorMessage
    checksum = data.checksum
}

fun EnterpriseMigrationEntity.toData() = EnterpriseMigrationData(
    id = id.value.toString(),
    enterpriseId = enterprise.id.value.toString(),
    version = version,
    description = description,
    script = script,
    status = status,
    appliedAt = appliedAt.toEpochMilli(),
    executionTime = executionTime,
    errorMessage = errorMessage,
    checksum = checksum,
)
