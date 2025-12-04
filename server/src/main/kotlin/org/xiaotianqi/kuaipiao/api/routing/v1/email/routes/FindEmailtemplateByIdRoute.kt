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
import org.xiaotianqi.kuaipiao.api.routing.v1.email.FindEmailtemplateByIdRoute
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*

fun Route.findEmailtemplateByIdRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        get<FindEmailtemplateByIdRoute> {
            try {
                val templateId = call.parameters["templateId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                val template = resendClient.getTemplate(templateId)

                if (template != null) {
                    call.respond(HttpStatusCode.OK, template)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Template not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
