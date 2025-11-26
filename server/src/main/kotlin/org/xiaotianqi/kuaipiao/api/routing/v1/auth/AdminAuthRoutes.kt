package org.xiaotianqi.kuaipiao.api.routing.v1.auth


import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminAddRoleUserRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminRegisterRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminProfileRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminRemoveUserRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminUpdateStatusRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminUserByIdRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.admin.adminUserRoutes
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/sign-up")
@ApiRoute(
    method = "POST",
    summary = "Register super admin",
    tag = "Admin Authentication"
)
class AdminRegisterRoute

@Resource("/profile")
@ApiRoute(
    method = "GET",
    summary = "Get admin profile",
    tag = "Admin Authentication",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminProfileRoute

@Resource("/users")
@ApiRoute(
    method = "GET",
    summary = "List all users",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminUserRoute

@Resource("/users/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get user by ID",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminUserByIdRoute

@Resource("/users/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update user status",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminUpdateStatusRoute

@Resource("/users/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete user",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminRemoveUserRoute

@Resource("/users/{id}/roles")
@ApiRoute(
    method = "POST",
    summary = "Assign role to user",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth"
)
class AdminAddRoleUserRoute

@ExperimentalTime
fun Route.adminAuthRoutesV1() {
    route("/oauth/admin") {
        adminRegisterRoutes()
        adminProfileRoutes()
        adminUserRoutes()
        adminUserByIdRoutes()
        adminUpdateStatusRoutes()
        adminRemoveUserRoutes()
        adminAddRoleUserRoutes()
    }
}
