package org.xiaotianqi.kuaipiao.api.routing.v1.auth

import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.logoutRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.passwordOperationRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.emailVerificationRoutes
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.loginRoute
import org.xiaotianqi.kuaipiao.api.routing.v1.auth.routes.registerRoute
import io.ktor.resources.*
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*
import kotlin.time.ExperimentalTime

@Resource("/sign-up")
class RegisterRoute

@Resource("/sign-in")
class LoginRoute

@Resource("/logout")
class LogoutRoute

@Resource("/verify-email")
class VerifyEmailRoute(val token: String)

@Resource("/is-email-verified")
class IsEmailVerifiedRoute

@Resource("/password-forgotten")
class PasswordForgottenRoute(val email: String)

@Resource("/reset-password")
class ResetPasswordRoute(val token: String)

@Resource("/verification-notification")
class SendVerificationEmailRoute

@ExperimentalTime
fun Route.authRoutesV1() {
    route("/oauth") {
        registerRoute()
        loginRoute()
        emailVerificationRoutes()
        passwordOperationRoutes()
        authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
            logoutRoutes()
        }
    }
}
