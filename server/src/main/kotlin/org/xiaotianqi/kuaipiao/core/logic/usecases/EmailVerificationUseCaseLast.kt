package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.xiaotianqi.kuaipiao.core.clients.BrevoClient
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.data.daos.auth.EmailVerificationDao
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.xiaotianqi.kuaipiao.domain.email.EmailVerificationData
import kotlin.time.ExperimentalTime


@ExperimentalTime
object EmailVerificationUseCaseLast : KoinComponent {
    private val emailVerificationDao by inject<EmailVerificationDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val brevoClient by inject<BrevoClient>()

    /**
     * Sends a verification email to the provided email
     * and returns true if the email was sent successfully, false otherwise
     *
     * @return true if the email was sent, false otherwise
     */
    suspend fun createAndSend(user: UserData): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val emailVerificationData = EmailVerificationData(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000,
            createdAt = DatetimeUtils.currentMillis(),
        )

        val sent = brevoClient.sendEmailVerificationEmail(user.email, token)

        if (sent) {
            emailVerificationDao.create(emailVerificationData)
        }

        return sent
    }
}