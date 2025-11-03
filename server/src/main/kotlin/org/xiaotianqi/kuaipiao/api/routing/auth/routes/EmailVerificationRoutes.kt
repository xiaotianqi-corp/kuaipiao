package org.xiaotianqi.kuaipiao.api.routing.auth.routes

import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.plugins.UserIdPrincipalForEmailVerificationAuth
import org.xiaotianqi.kuaipiao.api.routing.auth.IsEmailVerifiedRoute
import org.xiaotianqi.kuaipiao.api.routing.auth.SendVerificationEmailRoute
import org.xiaotianqi.kuaipiao.api.routing.auth.VerifyEmailRoute
import org.xiaotianqi.kuaipiao.config.BrevoConfig
import org.xiaotianqi.kuaipiao.core.logic.usecases.EmailVerificationUseCase
import org.xiaotianqi.kuaipiao.data.daos.auth.EmailVerificationDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.emailVerificationRoutes() {
    val userDao by inject<UserDao>()
    val emailVerificationDao by inject<EmailVerificationDao>()

    authenticate(AuthenticationMethods.EMAIL_VERIFICATION_FORM_AUTH) {
        post<SendVerificationEmailRoute> {
            val principal = call.principal<UserIdPrincipalForEmailVerificationAuth>()
                ?: return@post call.respond(HttpStatusCode.Forbidden)

            val userIdString = principal.id.toString()

            val userDto = userDao.get(userIdString)
                ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified) {
                return@post call.respond(HttpStatusCode.OK)
            }

            if (emailVerificationDao.isUserRateLimited(userIdString)) {
                return@post call.respond(HttpStatusCode.TooManyRequests)
            }

            val emailSent = EmailVerificationUseCase.createAndSend(userDto)

            if (emailSent) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post<IsEmailVerifiedRoute> {
            val principal = call.principal<UserIdPrincipalForEmailVerificationAuth>()
                ?: return@post call.respond(HttpStatusCode.Forbidden)

            val userIdString = principal.id.toString()

            val userDto = userDao.get(userIdString)
                ?: return@post call.respond(HttpStatusCode.Forbidden)

            if (userDto.emailVerified) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

    get<VerifyEmailRoute> { request ->
        val emailVerificationDto = emailVerificationDao.get(request.token)
            ?: return@get call.respondRedirect(BrevoConfig.emailVerificationErrorUrl)

        val userDto = userDao.get(emailVerificationDto.userId)
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        if (userDto.emailVerified) {
            return@get call.respondRedirect(BrevoConfig.emailVerificationSuccessUrl)
        }

        val userIdAsDtId = DtId<UserData>(userDto.id)

        userDao.verifyEmail(userIdAsDtId)
        emailVerificationDao.deleteAllOfUser(userDto.id)
        call.respondRedirect(BrevoConfig.emailVerificationSuccessUrl)

    }
}
