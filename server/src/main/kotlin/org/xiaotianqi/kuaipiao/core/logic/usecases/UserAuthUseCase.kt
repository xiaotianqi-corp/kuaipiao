package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Clock
import kotlin.time.Duration

@Single
@ExperimentalTime
object UserAuthUseCase {
    fun isIncompleteAccountOutdated(user: UserData): Boolean {
        val now = Clock.System.now()
        val timeElapsed: Duration = now - user.createdAt
        return !user.emailVerified && timeElapsed > 7.days
    }
}
