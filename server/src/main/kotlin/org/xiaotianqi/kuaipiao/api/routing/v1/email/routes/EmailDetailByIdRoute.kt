package org.xiaotianqi.kuaipiao.api.routing.v1.email.routes

import org.xiaotianqi.kuaipiao.api.routing.v1.company.CompanyCreateRoute
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
import org.xiaotianqi.kuaipiao.api.routing.v1.email.EmailDetailByIdRoute

fun Route.emailDetailByIdRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        // Get email details
        get<EmailDetailByIdRoute> {
            try {
                val emailId = call.parameters["emailId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                val email = resendClient.getEmail(emailId)

                if (email != null) {
                    call.respond(HttpStatusCode.OK, email)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Email not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}