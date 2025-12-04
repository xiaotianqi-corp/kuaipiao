package org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.annotation.Single
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.RegisterRoute
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.core.logic.usecases.EmailVerificationUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.UserAuthUseCase
import org.xiaotianqi.kuaipiao.data.daos.enterprise.EnterpriseDao
import org.xiaotianqi.kuaipiao.data.daos.organization.OrganizationDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.RegistrationCredentials
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.domain.organization.OrganizationCreateData
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
@ExperimentalTime
fun Route.registerRoute() {

    val userDao by inject<UserDao>()
    val enterpriseDao by inject<EnterpriseDao>()
    val organizationDao by inject<OrganizationDao>()
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

        val email = signupData.email
        val subdomain = email.substringBefore("@")
        val hashedPassword = passwordEncoder.encode(signupData.password)

        val enterpriseId = signupData.enterpriseId ?: UUID.randomUUID().toString()

        if (signupData.enterpriseId == null) {
            val enterprise = EnterpriseCreateData(
                id = enterpriseId,
                subdomain = subdomain,
                domain = null,
                plan = EnterprisePlan.FREE,
                status = EntityStatus.ACTIVE,
                settings = "{}",
                metadata = "{}",
                createdAt = Clock.System.now()
            )
            enterpriseDao.create(enterprise)
        }

        val user = UserCreateData(
            id = UUID.randomUUID().toString(),
            username = subdomain,
            firstName = signupData.firstName,
            lastName = signupData.lastName,
            email = email,
            emailVerified = false,
            passwordHash = hashedPassword,
            enterpriseId = enterpriseId,
            roleIds = emptyList(),
            createdAt = Clock.System.now()
        )

        val userEntity = userDao.createAndReturnEntity(user)

        val organizationId = UUID.randomUUID().toString()
        val orgRequest = signupData.organization

        val organization = OrganizationCreateData(
            id = organizationId,
            userIds = listOf(userEntity.id.value.toString()),
            name = orgRequest?.name ?: subdomain,
            code = orgRequest?.code ?: subdomain,
            address = orgRequest?.address ?: "",
            email = orgRequest?.email ?: "",
            phone = orgRequest?.phone ?: "",
            country = orgRequest?.country ?: "",
            city = orgRequest?.city ?: "",
            status = orgRequest?.status ?: EntityStatus.ACTIVE,
            metadata = orgRequest?.metadata ?: "{}",
            createdAt = Clock.System.now()
        )

        val orgEntity = organizationDao.create(organization, listOf(userEntity))

        val emailSent = EmailVerificationUseCase.createAndSend(
            UserData(
                id = user.id,
                username = user.username,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                emailVerified = user.emailVerified,
                passwordHash = user.passwordHash,
                enterpriseId = user.enterpriseId,
                organizationIds = listOf(orgEntity.id.toString()),
                roleIds = user.roleIds,
                isActive = true,
                createdAt = user.createdAt
            )
        )

        if (emailSent) {
            call.respond(HttpStatusCode.OK, "Verification email sent")
        } else {
            call.respond(HttpStatusCode.Created, "User created (email not sent)")
        }
    }
}