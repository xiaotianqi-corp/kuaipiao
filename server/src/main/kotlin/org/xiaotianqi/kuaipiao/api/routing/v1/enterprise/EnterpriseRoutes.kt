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
    summary = "Create a new enterprise",
    tag = "Enterprise",
    requiresAuth = true,
    requestSchema = "CreateEnterpriseRequest",
    responseSchema = "EnterpriseResponse",
    exampleRequest = """{"name":"Tech Corp","subdomain":"techcorp","plan":"professional"}""",
    exampleResponse = """{"id":"ent-001","name":"Tech Corp","subdomain":"techcorp","status":"active","plan":"professional","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class EnterpriseCreateRoute

@Resource("/find/{id}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve enterprise by ID",
    tag = "Enterprise",
    requiresAuth = true,
    responseSchema = "EnterpriseResponse",
    exampleResponse = """{"id":"ent-001","name":"Tech Corp","subdomain":"techcorp","status":"active","plan":"professional","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class EnterpriseSearchByIdRoute(val id: String)

@Resource("/find/subdomain/{subdomain}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve enterprise by subdomain",
    tag = "Enterprise",
    requiresAuth = true,
    responseSchema = "EnterpriseResponse",
    exampleResponse = """{"id":"ent-001","name":"Tech Corp","subdomain":"techcorp","status":"active","plan":"professional","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class EnterpriseSearchBySubdomainRoute(val subdomain: String)

@Resource("/update/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update enterprise status",
    tag = "Enterprise",
    requiresAuth = true,
    requestSchema = "UpdateEnterpriseStatusRequest",
    responseSchema = "EnterpriseResponse",
    exampleRequest = """{"status":"suspended"}""",
    exampleResponse = """{"id":"ent-001","name":"Tech Corp","subdomain":"techcorp","status":"suspended","plan":"professional","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T10:00:00Z"}"""
)
class EnterpriseUpdateStatusRoute(val id: String)

@Resource("/update/{id}/plan")
@ApiRoute(
    method = "PUT",
    summary = "Update enterprise subscription plan",
    tag = "Enterprise",
    requiresAuth = true,
    requestSchema = "UpdateEnterprisePlanRequest",
    responseSchema = "EnterpriseResponse",
    exampleRequest = """{"plan":"enterprise"}""",
    exampleResponse = """{"id":"ent-001","name":"Tech Corp","subdomain":"techcorp","status":"active","plan":"enterprise","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T10:00:00Z"}"""
)
class EnterpriseUpdatePlanRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete enterprise by ID",
    tag = "Enterprise",
    requiresAuth = true,
    responseSchema = "DeleteMessageResponse",
    exampleResponse = """{"message":"Enterprise deleted successfully"}"""
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