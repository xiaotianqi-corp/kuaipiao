package org.xiaotianqi.kuaipiao.api.routing.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.auth.RegisterRoute
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.usecases.EmailVerificationUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.UserAuthUseCase
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.RegistrationCredentials
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import java.util.UUID

fun Route.registerRoute() {
    val userDao by inject<UserDao>()
    val passwordEncoder by inject<PasswordEncoder>()

    post<RegisterRoute> {
        val signupData = call.receive<RegistrationCredentials>()
        val existingUser = userDao.getFromEmail(signupData.email)

        if (existingUser != null) {
            if (UserAuthUseCase.isIncompleteAccountOutdated(existingUser)) {
                userDao.delete(DtId(UUID.fromString(existingUser.id)))
            } else {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
        }

        val hashedPassword = passwordEncoder.encode(signupData.password)
        val user = UserCreateData(
            id = UUID.randomUUID().toString(),
            email = signupData.email,
            passwordHash = hashedPassword,
            emailVerified = false,
            creationTimestamp = DatetimeUtils.currentMillis()
        )

        userDao.create(user)

        val emailSent = EmailVerificationUseCase.createAndSend(UserData(
            id = user.id,
            email = user.email,
            passwordHash = hashedPassword,
            emailVerified = user.emailVerified,
            creationTimestamp = user.creationTimestamp
        ))

        if (emailSent) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Created)
        }
    }
}
