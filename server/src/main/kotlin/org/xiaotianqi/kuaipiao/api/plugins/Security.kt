package org.xiaotianqi.kuaipiao.api.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.sessions.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.xiaotianqi.kuaipiao.api.security.JwtService
import org.xiaotianqi.kuaipiao.config.ApiConfig
import org.xiaotianqi.kuaipiao.data.daos.auth.UserSessionDao
import org.xiaotianqi.kuaipiao.data.daos.user.UserDao
import org.xiaotianqi.kuaipiao.domain.auth.UserSessionCookie
import org.xiaotianqi.kuaipiao.utils.DateTimeUtils
import org.xiaotianqi.kuaipiao.core.logic.PasswordEncoder
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserAuthSessionData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.core.logic.typedId.serialization.IdKotlinXSerializationModule
import io.ktor.util.pipeline.*
import org.xiaotianqi.kuaipiao.core.exceptions.AuthenticationException
import kotlin.time.ExperimentalTime

object AuthenticationMethods {
    const val EMAIL_VERIFICATION_FORM_AUTH = "email_verification_form_auth"
    const val USER_SESSION_AUTH = "user_session_auth"
}

@ExperimentalTime
data class UserIdPrincipalForEmailVerificationAuth(val id: DtId<UserData>)

@ExperimentalTime
fun Application.configureSecurity() {
    val userDao by inject<UserDao>()
    val userSessionDao by inject<UserSessionDao>()
    val passwordEncoder by inject<PasswordEncoder>()
    val jwtService by inject<JwtService>()

    install(Sessions) {
        cookie<UserSessionCookie>("user_session_id") {
            cookie.path = "/"
            cookie.secure = ApiConfig.cookieSecure
            cookie.httpOnly = true
            cookie.maxAgeInSeconds = ApiConfig.sessionMaxAgeInSeconds
            cookie.extensions["SameSite"] = "None"
            serializer = KotlinxSessionSerializer(
                Json {
                    serializersModule = IdKotlinXSerializationModule
                }
            )
        }
    }

    install(Authentication) {

        /** SUPER ADMIN JWT **/
        jwt("admin-realm") {
            realm = "kuaipiao"
            verifier(jwtService.verifier())
            authHeader {
                it.request.parseAuthorizationHeader()
            }

            validate { credential ->
                val roles = credential.payload.getClaim("roles").asList(String::class.java)

                if (roles?.contains("SUPER_ADMIN") == true)
                    JWTPrincipal(credential.payload)
                else null
            }

        }

        /** FORM LOGIN **/
        form(AuthenticationMethods.EMAIL_VERIFICATION_FORM_AUTH) {
            userParamName = "email"
            passwordParamName = "password"

            validate { credentials ->
                userDao.getFromEmail(credentials.name)?.let {
                    if (passwordEncoder.matches(credentials.password, it.passwordHash))
                        UserIdPrincipalForEmailVerificationAuth(DtId(it.id))
                    else null
                }
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        /** SESSION LOGIN **/
        session<UserSessionCookie>(AuthenticationMethods.USER_SESSION_AUTH) {
            validate { cookie ->
                val session = userSessionDao.get(cookie.userId, cookie.sessionId)

                if (session == null || DateTimeUtils.isExpired(session.iat))
                    null
                else session
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}


fun PipelineContext<Unit, ApplicationCall>.authSessionData(): UserAuthSessionData? = call.principal<UserAuthSessionData>()

fun PipelineContext<Unit, ApplicationCall>.authSessionDataOrThrow(): UserAuthSessionData = authSessionData() ?: throw AuthenticationException()

@ExperimentalTime
fun PipelineContext<Unit, ApplicationCall>.userIdFromSession(): DtId<UserData>? =
    call.principal<UserAuthSessionData>()?.userId?.let { DtId<UserData>(it) }

@ExperimentalTime
fun PipelineContext<Unit, ApplicationCall>.userIdFromSessionOrThrow(): DtId<UserData> = userIdFromSession() ?: throw AuthenticationException()
