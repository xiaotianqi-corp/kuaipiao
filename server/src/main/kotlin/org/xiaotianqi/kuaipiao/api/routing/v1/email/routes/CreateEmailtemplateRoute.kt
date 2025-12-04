package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailtemplateRoute


fun Route.createEmailtemplateRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<CreateEmailtemplateRoute> {
            try {
                val dto = call.receive<CreateTemplateDTO>()

                val request = ResendCreateTemplateRequest(
                    name = dto.name,
                    html = dto.html,
                    alias = dto.alias,
                    from = dto.from,
                    subject = dto.subject,
                    reply_to = dto.replyTo,
                    text = dto.text,
                )

                val template = resendClient.createTemplate(request)

                if (template != null) {
                    call.respond(HttpStatusCode.Created, template)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create template"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}