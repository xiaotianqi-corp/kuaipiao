package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailWebhookRoute
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*

fun Route.createEmailWebhookRoute() {

    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<CreateEmailWebhookRoute> {

            try {
                val dto = call.receive<CreateWebhookDTO>()

                println("Webhook recibido: $dto")

                call.respond(
                    status = HttpStatusCode.OK,
                    message = mapOf("status" to "received")
                )

            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to (e.message ?: "Invalid payload"))
                )
            }
        }
    }
}