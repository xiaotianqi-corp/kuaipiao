package org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import java.util.*
import kotlin.code

object RolesTable : UUIDTable("roles") {
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
}

object PermissionsTable : UUIDTable("permissions") {
    var code = varchar("code", 100).uniqueIndex()
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
}

object RolePermissionsTable : Table("role_permissions") {
    val role = reference("role_id", RolesTable, onDelete = ReferenceOption.CASCADE)
    val permission = reference("permission_id", PermissionsTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(role, permission)
}

object UserRolesTable : Table("user_roles") {
    val user = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val role = reference("role_id", RolesTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(user, role)
}

class RoleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleEntity>(RolesTable)
    var name by RolesTable.name
    var description by RolesTable.description
    var permissions by PermissionEntity via RolePermissionsTable
}

class PermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionEntity>(PermissionsTable)
    var code by PermissionsTable.code
    var name by PermissionsTable.name
    var description by PermissionsTable.description
}
