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
import org.xiaotianqi.kuaipiao.config.ResendConfig
import org.xiaotianqi.kuaipiao.api.routing.v1.email.EmailBatchRoute

fun Route.emailBatchRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<EmailBatchRoute> {
            try {
                val dto = call.receive<SendBatchEmailDTO>()
                val config = ResendConfig

                val emailRequests = dto.emails.map { emailDto ->
                    ResendEmailRequest(
                        from = emailDto.from ?: config.getFromAddress(),
                        to = emailDto.to,
                        subject = emailDto.subject,
                        html = emailDto.html,
                        text = emailDto.text,
                        cc = emailDto.cc,
                        bcc = emailDto.bcc,
                        reply_to = emailDto.replyTo,
                        tags = emailDto.tags?.map {
                            ResendTag(
                                name = it["name"] ?: "",
                                value = it["value"] ?: ""
                            )
                        },
                    )
                }

                val emailIds = resendClient.sendBatchEmails(emailRequests)

                if (emailIds != null) {
                    call.respond(HttpStatusCode.Created, mapOf("ids" to emailIds, "count" to emailIds.size))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to send batch emails"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}