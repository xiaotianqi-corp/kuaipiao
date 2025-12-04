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
import org.xiaotianqi.kuaipiao.scripts.ApiRoute
import kotlin.time.ExperimentalTime

@Resource("/sign-up")
@ApiRoute(
    method = "POST",
    summary = "Register new user",
    tag = "Authentication",
    requestSchema = "RegistrationCredentials",
    responseSchema = "VerificationMessageResponse",
    exampleRequest = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "john@example.com",
          "password": "SecurePass123",
          "organization": {
            "id": "org-12345",
            "userIds": [],
            "name": "Acme Corp",
            "code": "ACME001",
            "address": "123 Main St",
            "phone": "+1-555-1234",
            "email": "contact@acme.com",
            "country": "USA",
            "city": "New York",
            "metadata": null,
            "status": "ACTIVE",
            "createdAt": "2024-01-01T00:00:00Z",
            "updatedAt": null
          }
        }
    """,
    exampleResponse = """{"message":"Verification email sent"}"""
)
class RegisterRoute

@Resource("/sign-in")
@ApiRoute(
    method = "POST",
    summary = "Authenticate user",
    tag = "Authentication",
    requestSchema = "LoginCredentials",
    responseSchema = "UserResponse",
    exampleRequest = """{"email":"user@example.com","password":"password123"}""",
    exampleResponse = """{"id":"123e4567-e89b-12d3-a456-426614174000","username":"user","email":"user@example.com","firstName":"John","lastName":"Doe","enterpriseId":"ent-123","organizationIds":[],"roleIds":[],"createdAt":"2024-01-01T00:00:00Z"}"""
)
class LoginRoute

@Resource("/logout")
@ApiRoute(
    method = "GET",
    summary = "Logout user",
    tag = "Authentication",
    requiresAuth = true,
    responseSchema = "MessageResponse",
    exampleResponse = """{"message":"Logout successful"}"""
)
class LogoutRoute

@Resource("/verify-email")
@ApiRoute(
    method = "GET",
    summary = "Verify email address with token",
    tag = "Authentication",
    responseSchema = "MessageResponse",
    exampleResponse = """{"message":"Email verified successfully"}"""
)
class VerifyEmailRoute(val token: String)

@Resource("/is-email-verified")
@ApiRoute(
    method = "POST",
    summary = "Check if email is verified",
    tag = "Authentication",
    requiresAuth = true,
    responseSchema = "VerificationStatusResponse",
    exampleResponse = """{"verified":true}"""
)
class IsEmailVerifiedRoute

@Resource("/password-forgotten")
@ApiRoute(
    method = "GET",
    summary = "Request password reset email",
    tag = "Authentication",
    responseSchema = "MessageResponse",
    exampleResponse = """{"message":"Password reset email sent"}"""
)
class PasswordForgottenRoute(val email: String)

@Resource("/reset-password")
@ApiRoute(
    method = "POST",
    summary = "Reset password with reset token",
    tag = "Authentication",
    requestSchema = "ResetPasswordRequest",
    responseSchema = "MessageResponse",
    exampleRequest = """{"password":"NewSecurePass123"}""",
    exampleResponse = """{"message":"Password reset successfully"}"""
)
class ResetPasswordRoute(val token: String)

@Resource("/verification-notification")
@ApiRoute(
    method = "POST",
    summary = "Send verification email",
    tag = "Authentication",
    requiresAuth = true,
    responseSchema = "MessageResponse",
    exampleResponse = """{"message":"Verification email sent"}"""
)
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