package org.xiaotianqi.kuaipiao.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionData

val RequiredPermissionsKey = AttributeKey<List<String>>("RequiredPermissions")

val AuthorizationPlugin = createApplicationPlugin("AuthorizationPlugin") {

    application.intercept(ApplicationCallPipeline.Call) {
        val requiredPermissions = call.attributes.getOrNull(RequiredPermissionsKey)

        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return@intercept
        }

        val session = call.sessions.get<UserSessionData>()

        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, "No authenticated session found.")
            return@intercept finish()
        }

        val userPermissions = session.permissions

        val hasAnyPermission = requiredPermissions.any { userPermissions.contains(it) }

        if (!hasAnyPermission) {
            call.respond(HttpStatusCode.Forbidden, "Permission denied. Requires one of: $requiredPermissions")
            return@intercept finish()
        }
    }
}

class PermissionsRouteSelector(private val permissions: List<String>) : RouteSelector() {

    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        context.call.attributes.put(RequiredPermissionsKey, permissions)
        return RouteSelectorEvaluation.Constant
    }
    
    override fun toString(): String = "(permissions: ${permissions.joinToString(", ")})"
}

fun Route.withPermissions(vararg permissions: String, build: Route.() -> Unit) {
    val authorizedRoute = this.createChild(PermissionsRouteSelector(permissions.toList()))
    authorizedRoute.build()
}