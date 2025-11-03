package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.xiaotianqi.kuaipiao.core.clients.BrevoClient
import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.data.daos.auth.PasswordResetDao
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData

object PasswordResetUseCase : KoinComponent {
    private val passwordResetDao by inject<PasswordResetDao>()
    private val tokenGenerator by inject<TokenGenerator>()
    private val brevoClient by inject<BrevoClient>()

    /**
     * Sends a password reset email to the provided [user]
     *
     * @returns true if the email was sent successfully, false otherwise
     */
    suspend fun createAndSend(user: UserData): Boolean {
        val (token, hashedToken) = tokenGenerator.generate()

        val passwordResetData = PasswordResetData(
            token = hashedToken,
            userId = user.id,
            expireAt = DatetimeUtils.currentMillis() + 3600000,
        )

        val sent = brevoClient.sendPasswordResetEmail(user.email, token)

        if (sent) {
            passwordResetDao.create(passwordResetData)
        }

        return sent
    }
}