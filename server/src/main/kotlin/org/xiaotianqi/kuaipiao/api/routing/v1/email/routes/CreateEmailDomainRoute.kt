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
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailDomainRoute
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*

fun Route.createEmailDomainRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<CreateEmailDomainRoute> {
            try {
                val body = call.receive<Map<String, String>>()
                val domainName = body["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val region = body["region"] ?: "us-east-1"

                val domain = resendClient.createDomain(domainName, region)

                if (domain != null) {
                    call.respond(HttpStatusCode.Created, domain)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create domain"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
