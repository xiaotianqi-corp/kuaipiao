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
    tag = "Admin Authentication",
    requestSchema = "CreateSuperAdminRequest",
    responseSchema = "MessageResponse",
    exampleRequest = """{"email":"admin@kuaipiao.com","password":"SecureAdminPass123"}""",
    exampleResponse = """{"message":"Super admin created successfully"}"""
)
class AdminRegisterRoute

@Resource("/profile")
@ApiRoute(
    method = "GET",
    summary = "Get admin profile",
    tag = "Admin Authentication",
    requiresAuth = true,
    authType = "JWTAuth",
    responseSchema = "UserResponse",
    exampleResponse = """{"id":"admin-001","username":"admin","email":"admin@kuaipiao.com","firstName":"Admin","lastName":"User","enterpriseId":"","organizationIds":[],"roleIds":["admin"],"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class AdminProfileRoute

@Resource("/users")
@ApiRoute(
    method = "GET",
    summary = "List all users",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth",
    responseSchema = "UserListResponse",
    exampleResponse = """{"users":[{"id":"user-001","username":"user","email":"user@example.com","firstName":"John","lastName":"Doe","enterpriseId":"ent-123","organizationIds":[],"roleIds":[],"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}],"total":1}"""
)
class AdminUserRoute

@Resource("/users/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get user by ID",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth",
    responseSchema = "UserResponse",
    exampleResponse = """{"id":"user-001","username":"user","email":"user@example.com","firstName":"John","lastName":"Doe","enterpriseId":"ent-123","organizationIds":[],"roleIds":[],"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class AdminUserByIdRoute

@Resource("/users/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update user status",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth",
    requestSchema = "UpdateUserStatusRequest",
    responseSchema = "UserResponse",
    exampleRequest = """{"status":"inactive"}""",
    exampleResponse = """{"id":"user-001","username":"user","email":"user@example.com","firstName":"John","lastName":"Doe","enterpriseId":"ent-123","organizationIds":[],"roleIds":[],"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T10:00:00Z"}"""
)
class AdminUpdateStatusRoute

@Resource("/users/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete user",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth",
    responseSchema = "MessageResponse",
    exampleResponse = """{"message":"User deleted successfully"}"""
)
class AdminRemoveUserRoute

@Resource("/users/{id}/roles")
@ApiRoute(
    method = "POST",
    summary = "Assign role to user",
    tag = "Admin User Management",
    requiresAuth = true,
    authType = "JWTAuth",
    requestSchema = "AssignRoleRequest",
    responseSchema = "UserResponse",
    exampleRequest = """{"roleId":"role-123"}""",
    exampleResponse = """{"id":"user-001","username":"user","email":"user@example.com","firstName":"John","lastName":"Doe","enterpriseId":"ent-123","organizationIds":[],"roleIds":["role-123"],"createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T10:00:00Z"}"""
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