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
import org.xiaotianqi.kuaipiao.api.routing.v1.email.CreateEmailContactRoute
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.domain.email.*

fun Route.createEmailContactRoute() {
    val resendClient by inject<ResendClient>()

    authenticate(AuthenticationMethods.BEARER_AUTH) {

        post<CreateEmailContactRoute> {
            try {
                val audienceId = call.parameters["audienceId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val dto = call.receive<CreateContactDTO>()

                val contact = resendClient.createContact(
                    audienceId = audienceId,
                    email = dto.email,
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                )

                if (contact != null) {
                    call.respond(HttpStatusCode.Created, contact)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create contact"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}