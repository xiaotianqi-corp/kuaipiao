package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import io.ktor.server.resources.get
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailContactRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailDomainRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailWebhookRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.email.FindAllEmailDomainRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.email.VerifyEmailDomainRoute
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*
import org.xiaotianqi.kuaipiao.config.ResendConfig

fun Route.createEmailWebhookRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {
        post<CreateEmailWebhookRoute> {
            try {
                val dto = call.receive<CreateWebhookDTO>()

                val webhook = resendClient.createWebhook(dto.endpoint, dto.events)

                if (webhook != null) {
                    call.respond(HttpStatusCode.Created, webhook)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create webhook"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}

// Helper functions to add to ResendClient
suspend fun ResendClient.createWebhook(endpoint: String, events: List<String>): ResendWebhookResponse? {
    val request = ResendCreateWebhookRequest(endpoint = endpoint, events = events)

    return try {
        val response = io.ktor.client.request.post("https://api.resend.com/webhooks") {
            header("Authorization", "Bearer ${ResendConfig.apiKey}")
            io.ktor.client.request.setBody(request)
        }

        if (response.status.isSuccess()) {
            response.body()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}