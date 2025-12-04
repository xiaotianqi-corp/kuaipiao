package org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserOrganizationsTable
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationData
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object OrganizationsTable : UUIDTable("organizations") {
    val name = varchar("name", 150)
    val code = varchar("code", 50)
    val address = varchar("address", 250)
    val phone = varchar("phone", 150)
    val email = varchar("email", 150)
    val country = varchar("country", 150)
    val city = varchar("city", 150)
    val status = enumerationByName("status", 50, EntityStatus::class)
    val metadata = text("metadata").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
}

class OrganizationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrganizationEntity>(OrganizationsTable)
    var name by OrganizationsTable.name
    var code by OrganizationsTable.code
    var address by OrganizationsTable.address
    var phone by OrganizationsTable.phone
    var email by OrganizationsTable.email
    var country by OrganizationsTable.country
    var city by OrganizationsTable.city
    var status by OrganizationsTable.status
    var metadata by OrganizationsTable.metadata
    var createdAt by OrganizationsTable.createdAt
    var updatedAt by OrganizationsTable.updatedAt

    var users by UserEntity via UserOrganizationsTable
}

@ExperimentalTime
fun OrganizationEntity.fromData(
    data: OrganizationData,
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

@ExperimentalTime
fun OrganizationEntity.toData() = OrganizationData(
    id = id.value.toString(),
    userIds = users.map { it.id.value.toString() },
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
    updatedAt = updatedAt?.toKotlinInstant()
)
