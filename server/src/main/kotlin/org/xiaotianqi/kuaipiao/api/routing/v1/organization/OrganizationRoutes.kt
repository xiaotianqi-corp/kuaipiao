package org.xiaotianqi.kuaipiao.api.routing.v1.organization

import io.ktor.resources.*
import io.ktor.server.routing.*
import org.xiaotianqi.kuaipiao.api.plugins.withPermissions
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationCreateRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationRemoveRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationSearchByCodeRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationSearchByIdRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.organization.routes.organizationUpdateStatusRoute
import kotlin.time.ExperimentalTime

@Resource("/create")
class OrganizationCreateRoute

@Resource("/find/id/{id}")
class OrganizationSearchByIdRoute(val id: String)

@Resource("/find/code/{code}")
class OrganizationSearchByCodeRoute(val code: String)

@Resource("/update/{id}/status")
class OrganizationUpdateStatusRoute(val id: String)

@Resource("/delete/{id}")
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
