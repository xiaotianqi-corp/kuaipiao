package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl

import org.xiaotianqi.kuaipiao.core.logic.DatetimeUtils
import org.xiaotianqi.kuaipiao.core.logic.TokenGenerator
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.PasswordResetDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.*
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime

class PasswordResetDBIImpl(
    private val tokenGenerator: TokenGenerator,
) : PasswordResetDBI {
    override suspend fun count(id: String): Long =
        dbQuery {
            val currentTimestamp = DatetimeUtils.currentJavaInstant()

            PasswordResetEntity.count(
                PasswordResetTable.user eq DtId<UserData>(id).toEntityId(UsersTable)
                        and (PasswordResetTable.expires_at greater currentTimestamp)
            )
        }

    override suspend fun create(passwordResetData: PasswordResetData) {
        dbQuery {
            PasswordResetEntity.new {
                fromData(passwordResetData)
            }
        }
    }

    override suspend fun get(token: String) =
        dbQuery {
            PasswordResetEntity
                .find { PasswordResetTable.token eq tokenGenerator.hashToken(token) }
                .limit(1)
                .firstOrNull()
                ?.toData()
        }

    override suspend fun deleteAll(id: DtId<UserData>) {
        dbQuery {
            PasswordResetTable.deleteWhere { user eq id.toEntityId(UsersTable) }
        }
    }

    override suspend fun deleteExpired() {
        dbQuery {
            val currentMillis = DatetimeUtils.currentJavaInstant()

            PasswordResetTable.deleteWhere {
                expires_at less currentMillis
            }
        }
    }
}
