package org.xiaotianqi.kuaipiao.core.logic

import org.koin.core.annotation.Factory
import org.mindrot.jbcrypt.BCrypt

@Factory
class PasswordEncoder {
    private val strength = 12

    fun encode(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(strength))
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, encodedPassword)
    }
}
