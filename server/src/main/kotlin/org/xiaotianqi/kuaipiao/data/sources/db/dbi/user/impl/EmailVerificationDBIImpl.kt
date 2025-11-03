package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl

import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.EmailVerificationDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.*
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.domain.email.EmailVerificationData
import org.xiaotianqi.kuaipiao.domain.auth.UserData

@Single(createdAtStart = true)
class EmailVerificationDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : EmailVerificationDBI {
    override suspend fun count(id: String): Long =
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            EmailVerificationEntity.count(
                EmailVerificationTable.user eq DtId<UserData>(id).toEntityId(UsersTable)
                        and (EmailVerificationTable.expires_at greater currentMillis)
            )
        }

    override suspend fun create(emailVerificationData: EmailVerificationData) {
        dbQuery {
            EmailVerificationEntity.new {
                fromData(emailVerificationData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            EmailVerificationEntity
                .find { EmailVerificationTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteAll(id: String) {
        dbQuery {
            EmailVerificationTable.deleteWhere {
                user eq DtId<UserData>(id).toEntityId(UsersTable)
            }
        }
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentTimestamp = DatetimeUtils.currentJavaInstant()

            EmailVerificationTable.deleteWhere {
                expires_at less currentTimestamp
            }
        }
    }
}
