package org.xiaotianqi.kuaipiao.api.routing.v1.enterprise

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseCreateRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseRemoveRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseSearchByIdRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseSearchBySubdomainRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseUpdateStatusRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.enterprise.routes.enterpriseUpdatePlanRoute
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/create")
@ApiRoute(
    method = "POST",
    summary = "Create enterprise",
    tag = "Enterprise",
    requiresAuth = true
)
class EnterpriseCreateRoute

@Resource("/find/{id}")
@ApiRoute(
    method = "GET",
    summary = "Get enterprise by ID",
    tag = "Enterprise",
    requiresAuth = true
)
class EnterpriseSearchByIdRoute(val id: String)

@Resource("/find/subdomain/{subdomain}")
@ApiRoute(
    method = "GET",
    summary = "Get enterprise by domain",
    tag = "Enterprise",
    requiresAuth = true
)
class EnterpriseSearchBySubdomainRoute(val subdomain: String)

@Resource("/update/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update enterprise status",
    tag = "Enterprise",
    requiresAuth = true
)
class EnterpriseUpdateStatusRoute(val id: String)

@Resource("/update/{id}/plan")
@ApiRoute(
    method = "PUT",
    summary = "Update enterprise plan",
    tag = "Enterprise",
    requiresAuth = true
)
class EnterpriseUpdatePlanRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete Enterprise",
    tag = "Enterprise",
    requiresAuth = true
)
data class EnterpriseRemoveRoute(val id: String)

@ExperimentalTime
fun Route.enterpriseRoutesV1() {
    route("/enterprise") {
        withPermissions("MANAGE", "CREATE", "UPDATE", "DELETE") {
            enterpriseCreateRoute()
            enterpriseRemoveRoute()
            enterpriseUpdateStatusRoute()
            enterpriseUpdatePlanRoute()
        }
        withPermissions("VIEW") {
            enterpriseSearchByIdRoute()
            enterpriseSearchBySubdomainRoute()
        }
    }
}
