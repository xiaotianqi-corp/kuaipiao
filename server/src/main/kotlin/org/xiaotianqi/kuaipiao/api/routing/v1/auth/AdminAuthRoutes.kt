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
import kotlin.time.ExperimentalTime

@Resource("/sign-up")
class AdminRegisterRoute

@Resource("/profile")
class AdminProfileRoute

@Resource("/users")
class AdminUserRoute

@Resource("/users/{id}")
class AdminUserByIdRoute

@Resource("/users/{id}/status")
class AdminUpdateStatusRoute

@Resource("/users/{id}")
class AdminRemoveUserRoute

@Resource("/users/{id}/roles")
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
