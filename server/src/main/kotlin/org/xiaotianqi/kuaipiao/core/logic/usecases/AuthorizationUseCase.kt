package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.daos.rbac.RoleDao
import org.xiaotianqi.kuaipiao.data.daos.rbac.PermissionDao
import org.xiaotianqi.kuaipiao.domain.rbac.RoleData
import org.xiaotianqi.kuaipiao.domain.rbac.PermissionData
import java.util.*

@Single
class AuthorizationUseCase(
    private val roleDao: RoleDao,
    private val permissionDao: PermissionDao
) {

    private suspend fun getUserPermissions(userRoleIds: List<String>): Set<String> {
        if (userRoleIds.isEmpty()) return emptySet()

        val roleIds = userRoleIds.mapNotNull { id ->
            runCatching { DtId<RoleData>(UUID.fromString(id)) }.getOrNull()
        }
        if (roleIds.isEmpty()) return emptySet()

        val userRoles = roleDao.getByIds(roleIds)
        if (userRoles.isEmpty()) return emptySet()

        val permissionIds = userRoles.flatMap { it.permissionIds }.distinct()
        if (permissionIds.isEmpty()) return emptySet()

        val permissionDtIds = permissionIds.mapNotNull { id ->
            runCatching { DtId<PermissionData>(UUID.fromString(id)) }.getOrNull()
        }

        val permissions = permissionDao.getByIds(permissionDtIds)


        return permissions.map { it.code.lowercase() }.toSet()
    }

    suspend fun userHasPermission(userRoleIds: List<String>, requiredPermission: String): Boolean {
        if (requiredPermission.isBlank()) return false
        val userPermissions = getUserPermissions(userRoleIds)
        return requiredPermission.lowercase() in userPermissions
    }

    suspend fun userHasAllPermissions(userRoleIds: List<String>, requiredPermissions: List<String>): Boolean {
        if (requiredPermissions.isEmpty()) return false
        val userPermissions = getUserPermissions(userRoleIds)
        return requiredPermissions.all { it.lowercase() in userPermissions }
    }

    suspend fun userHasAnyPermission(userRoleIds: List<String>, candidatePermissions: List<String>): Boolean {
        if (candidatePermissions.isEmpty()) return false
        val userPermissions = getUserPermissions(userRoleIds)
        return candidatePermissions.any { it.lowercase() in userPermissions }
    }
}
