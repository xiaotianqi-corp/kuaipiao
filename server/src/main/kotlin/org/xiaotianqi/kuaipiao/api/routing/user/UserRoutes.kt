package org.xiaotianqi.kuaipiao.api.routing.user

import org.xiaotianqi.kuaipiao.api.plugins.AuthenticationMethods
import org.xiaotianqi.kuaipiao.api.routing.user.routes.logoutRoutes
import org.xiaotianqi.kuaipiao.api.routing.user.routes.passwordOperationRoutes
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/logout")
class LogoutRoute

@Resource("/password-forgotten")
class PasswordForgottenRoute(val email: String)

@Resource("/reset-password")
class ResetPasswordRoute(val token: String)

fun Route.userRoutes() {
    passwordOperationRoutes()

    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        logoutRoutes()
    }
}
