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
    summary = "Create a new organization",
    tag = "Organization",
    requiresAuth = true,
    requestSchema = "CreateOrganizationRequest",
    responseSchema = "OrganizationResponse",
    exampleRequest = """{"name":"Engineering Dept","code":"ENG-001","enterpriseId":"ent-123"}""",
    exampleResponse = """{"id":"org-001","name":"Engineering Dept","code":"ENG-001","status":"active","enterpriseId":"ent-123","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class OrganizationCreateRoute

@Resource("/find/id/{id}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve organization by ID",
    tag = "Organization",
    requiresAuth = true,
    responseSchema = "OrganizationResponse",
    exampleResponse = """{"id":"org-001","name":"Engineering Dept","code":"ENG-001","status":"active","enterpriseId":"ent-123","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class OrganizationSearchByIdRoute(val id: String)

@Resource("/find/code/{code}")
@ApiRoute(
    method = "GET",
    summary = "Retrieve organization by code",
    tag = "Organization",
    requiresAuth = true,
    responseSchema = "OrganizationResponse",
    exampleResponse = """{"id":"org-001","name":"Engineering Dept","code":"ENG-001","status":"active","enterpriseId":"ent-123","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T00:00:00Z"}"""
)
class OrganizationSearchByCodeRoute(val code: String)

@Resource("/update/{id}/status")
@ApiRoute(
    method = "PUT",
    summary = "Update organization status",
    tag = "Organization",
    requiresAuth = true,
    requestSchema = "UpdateOrganizationStatusRequest",
    responseSchema = "OrganizationResponse",
    exampleRequest = """{"status":"inactive"}""",
    exampleResponse = """{"id":"org-001","name":"Engineering Dept","code":"ENG-001","status":"inactive","enterpriseId":"ent-123","createdAt":"2024-01-01T00:00:00Z","updatedAt":"2024-01-01T10:00:00Z"}"""
)
class OrganizationUpdateStatusRoute(val id: String)

@Resource("/delete/{id}")
@ApiRoute(
    method = "DELETE",
    summary = "Delete organization by ID",
    tag = "Organization",
    requiresAuth = true,
    responseSchema = "DeleteMessageResponse",
    exampleResponse = """{"message":"Organization deleted successfully"}"""
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