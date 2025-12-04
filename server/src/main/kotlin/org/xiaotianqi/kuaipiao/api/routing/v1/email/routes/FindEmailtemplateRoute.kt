package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.get
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*
import org.xiaotianqi.kuaipiao.api.routing.v1.email.FindEmailtemplateRoute

fun Route.findEmailtemplateRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        get<FindEmailtemplateRoute> {
            try {
                val templates = resendClient.listTemplates()

                if (templates != null) {
                    call.respond(HttpStatusCode.OK, mapOf("data" to templates))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to list templates"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}