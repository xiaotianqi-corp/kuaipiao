package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.routing.v1.email.EmailSendRoute
import org.xiaotianqi.kuaipiao.config.ResendConfig
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*

fun Route.emailSendRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {
        post<EmailSendRoute>{
            try {
                val dto = call.receive<SendEmailDTO>()
                val config = ResendConfig

                println("DEBUG: Received DTO: $dto")
                println("DEBUG: html=${dto.html}, text=${dto.text}")

                if (dto.html == null && dto.text == null) {
                    return@post call.respond(HttpStatusCode.BadRequest,
                        mapOf("error" to "html or text is required"))
                }

                val emailRequest = ResendEmailRequest(
                    from = dto.from ?: config.getFromAddress(),
                    to = dto.to,
                    subject = dto.subject,
                    html = dto.html,
                    text = dto.text,
                    cc = dto.cc,
                    bcc = dto.bcc,
                    reply_to = dto.replyTo,
                    tags = dto.tags?.map {
                        ResendTag(
                            name = it["name"] ?: "",
                            value = it["value"] ?: ""
                        )
                    },
                )

                println("DEBUG: Sending request: $emailRequest")
                val emailId = resendClient.sendEmail(emailRequest)

                if (emailId != null) {
                    call.respond(HttpStatusCode.Created, mapOf("id" to emailId))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to send email"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}