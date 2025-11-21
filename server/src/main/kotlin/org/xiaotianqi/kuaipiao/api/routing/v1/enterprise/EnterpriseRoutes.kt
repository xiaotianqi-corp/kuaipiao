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
import kotlin.time.ExperimentalTime

@Resource("/create")
class EnterpriseCreateRoute

@Resource("/find/{id}")
class EnterpriseSearchByIdRoute(val id: String)

@Resource("/find/subdomain/{subdomain}")
class EnterpriseSearchBySubdomainRoute(val subdomain: String)

@Resource("/update/{id}/status")
class EnterpriseUpdateStatusRoute(val id: String)

@Resource("/update/{id}/plan")
class EnterpriseUpdatePlanRoute(val id: String)

@Resource("/delete/{id}")
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
