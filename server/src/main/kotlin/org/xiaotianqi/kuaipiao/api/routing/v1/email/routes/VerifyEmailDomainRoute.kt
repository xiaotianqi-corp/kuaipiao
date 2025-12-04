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

fun Route.verifyEmailDomainRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<VerifyEmailDomainRoute> {
            try {
                val domainId = call.parameters["domainId"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                val verified = resendClient.verifyDomain(domainId)

                if (verified) {
                    call.respond(HttpStatusCode.OK, mapOf("verified" to true))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to verify domain"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
