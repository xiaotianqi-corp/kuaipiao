package org.xiaotianqi.kuaipiao.data.sources.db.schemas.user

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RoleEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.UserRolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterprisesTable
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object UsersTable : UUIDTable("users") {
    val username = varchar("username", 100).nullable()
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val emailVerified = bool("email_verified").default(false)
    val passwordHash = varchar("password_hash", 255)
    val enterprise = reference("enterprise_id", EnterprisesTable).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
    val lastLoginAt = timestamp("last_login_at").nullable()
}

object UserOrganizationsTable : Table("user_organizations") {
    val user = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val organization = reference("organization_id", OrganizationsTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(user, organization)}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)
    var username by UsersTable.username
    var firstName by UsersTable.firstName
    var lastName by UsersTable.lastName
    var email by UsersTable.email
    var emailVerified by UsersTable.emailVerified
    var passwordHash by UsersTable.passwordHash
    var enterprise by EnterpriseEntity optionalReferencedOn UsersTable.enterprise
    var isActive by UsersTable.isActive
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
    var lastLoginAt by UsersTable.lastLoginAt

    var organizations by OrganizationEntity via UserOrganizationsTable
    var roles by RoleEntity via UserRolesTable
}

@ExperimentalTime
fun UserEntity.fromData(
    data: UserData,
    enterpriseEntity: EnterpriseEntity,
    organizationEntities: List<OrganizationEntity> = emptyList(),
    rolesEntities: List<RoleEntity> = emptyList()
) {
    username = data.username
    firstName = data.firstName
    lastName = data.lastName
    email = data.email
    emailVerified = data.emailVerified
    passwordHash = data.passwordHash
    enterprise = enterpriseEntity
    organizations = SizedCollection(organizationEntities)
    roles = SizedCollection(rolesEntities)
    isActive = data.isActive
    createdAt = data.createdAt.toJavaInstant()
    updatedAt = data.updatedAt?.toJavaInstant()
    lastLoginAt = data.lastLoginAt?.toJavaInstant()
}

@ExperimentalTime
fun UserEntity.toData() = UserData(
    id = id.value.toString(),
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    emailVerified = emailVerified,
    passwordHash = passwordHash,
    enterpriseId = enterprise?.id?.value.toString(),
    organizationIds = organizations.map { it.id.value.toString() },
    roleIds = roles.map { it.id.value.toString() },
    isActive = isActive,
    createdAt = createdAt.toKotlinInstant(),
    updatedAt = updatedAt?.toKotlinInstant(),
    lastLoginAt = lastLoginAt?.toKotlinInstant(),
)
