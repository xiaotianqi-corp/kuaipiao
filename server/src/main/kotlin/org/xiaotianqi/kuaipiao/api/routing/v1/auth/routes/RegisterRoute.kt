package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes

import org.xiaotianqi.kuaipiao.api.routing.v1.auth.RegisterRoute
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.usecases.EmailVerificationUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.UserAuthUseCase
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.RegistrationCredentials
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
@ExperimentalTime
fun Route.registerRoute() {
    val userDao by inject<UserDao>()
    val enterpriseDao by inject<EnterpriseDao>()
    val passwordEncoder by inject<PasswordEncoder>()

    post<RegisterRoute> {
        val signupData = call.receive<RegistrationCredentials>()
        val existingUser = userDao.getFromEmail(signupData.email)

        if (existingUser != null) {
            if (UserAuthUseCase.isIncompleteAccountOutdated(existingUser)) {
                userDao.delete(DtId(UUID.fromString(existingUser.id)))
            } else {
                call.respond(HttpStatusCode.Forbidden, "User already exists")
                return@post
            }
        }

        val hashedPassword = passwordEncoder.encode(signupData.password)

        val enterpriseId = signupData.enterpriseId ?: UUID.randomUUID().toString()

        if (signupData.enterpriseId == null) {
            val enterprise = EnterpriseCreateData(
                id = enterpriseId,
                metadata = "{}",
                settings = "{}",
                subdomain = signupData.email.substringBefore("@"),
                plan = EnterprisePlan.FREE,
                createdAt = Clock.System.now()
            )
            enterpriseDao.create(enterprise)
        }

        val user = UserCreateData(
            id = UUID.randomUUID().toString(),
            username = signupData.email.substringBefore("@"),
            firstName = signupData.firstName,
            lastName = signupData.lastName,
            enterpriseId = enterpriseId,
            email = signupData.email,
            passwordHash = hashedPassword,
            emailVerified = false,
            createdAt = Clock.System.now()
        )

        userDao.create(user)

        val emailSent = EmailVerificationUseCase.createAndSend(
            UserData(
                id = user.id,
                username = user.username,
                firstName = user.firstName,
                lastName = user.lastName,
                enterpriseId = user.enterpriseId,
                email = user.email,
                passwordHash = hashedPassword,
                emailVerified = user.emailVerified,
                isActive = true,
                createdAt = user.createdAt
            )
        )

        if (emailSent) {
            call.respond(HttpStatusCode.OK, "Verification email sent")
        } else {
            call.respond(HttpStatusCode.Created, "User created without verification email")
        }
    }
}
