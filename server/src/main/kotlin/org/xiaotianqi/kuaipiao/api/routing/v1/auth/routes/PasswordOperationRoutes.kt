package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.v1.auth.PasswordForgottenRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.ResetPasswordRoute
import org.xiaotianqi.kuaipiao.core.clients.BrevoClient
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.usecases.PasswordResetUseCase
import org.xiaotianqi.kuaipiao.data.daos.auth.PasswordResetDao
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetRequest
import java.util.UUID
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Route.passwordOperationRoutes() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordResetDao by inject<PasswordResetDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val brevoClient by inject<BrevoClient>()

    get<PasswordForgottenRoute> { request ->
        val user = userDao.getFromEmail(request.email)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        if (passwordResetDao.isUserRateLimited(user.id)) {
            return@get call.respond(HttpStatusCode.TooManyRequests)
        }

        val sentEmail = PasswordResetUseCase.createAndSend(user)

        if (sentEmail) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    post<ResetPasswordRoute> { request ->
        val passwordResetDto = passwordResetDao.get(request.token)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val user = userDao.get(passwordResetDto.userId)
            ?: return@post call.respond(HttpStatusCode.NotFound)

        val newPassword = call.receive<PasswordResetRequest>().password
        val newPasswordHashed = passwordEncoder.encode(newPassword)

        userDao.resetPassword(
            id = DtId(UUID.fromString(passwordResetDto.userId)),
            newPasswordHashed = newPasswordHashed,
            verifyEmail = true
        )

        userSessionDao.deleteAllOfUser(DtId(UUID.fromString(passwordResetDto.userId)))
        brevoClient.sendPasswordResetSuccessEmail(user.email)

        call.respond(HttpStatusCode.OK)
    }
}
