package org.xiaotianqi.kuaipiao.core.logic.usecases

import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import kotlin.time.Duration.Companion.days

object UserAuthUseCase {
    /**
     * If an account didn't verify its email for more than 7 days it's considered non-existent
     */
    fun isIncompleteAccountOutdated(user: UserData): Boolean {
        return !user.emailVerified && (DatetimeUtils.currentMillis() - user.creationTimestamp) > 7.days.inWholeMilliseconds
    }
}
