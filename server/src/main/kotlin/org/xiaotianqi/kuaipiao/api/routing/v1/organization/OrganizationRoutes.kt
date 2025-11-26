package org.xiaotianqi.kuaipiao.api.routing.v1.organization

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationCreateRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationRemoveRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationSearchByCodeRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationSearchByIdRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationUpdateStatusRoute
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/create")
@ApiRoute(
    method = "POST",
    summary = "Create organization",
    tag = "Organization",
    requiresAuth = true
)
class OrganizationCreateRoute

@Resource("/find/id/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get organization by ID",
    tag = "Organization",
    requiresAuth = true
)
class OrganizationSearchByIdRoute(val id: String)

@Resource("/find/code/{code}")
@ApiRoute(
    method = "GET",
    summary = "Get organization by code",
    tag = "Organization",
    requiresAuth = true
)
class OrganizationSearchByCodeRoute(val code: String)

@Resource("/update/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update organization status",
    tag = "Organization",
    requiresAuth = true
)
class OrganizationUpdateStatusRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete organization",
    tag = "Organization",
    requiresAuth = true
)
data class OrganizationRemoveRoute(val id: String)

@ExperimentalTime
fun Route.organizationRoutesV1() {
    route("/org") {
        withPermissions("MANAGE", "CREATE", "UPDATE", "DELETE") {
            organizationCreateRoute()
            organizationRemoveRoute()
            organizationUpdateStatusRoute()
        }

        withPermissions("VIEW") {
            organizationSearchByIdRoute()
            organizationSearchByCodeRoute()
        }
    }
}
