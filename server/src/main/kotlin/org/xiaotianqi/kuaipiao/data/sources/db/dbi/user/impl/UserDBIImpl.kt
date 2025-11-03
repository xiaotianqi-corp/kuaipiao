package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.UserDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.xiaotianqi.kuaipiao.data.mappers.fromCreateData
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import java.util.UUID

@Single(createdAtStart = true)
class UserDBIImpl : UserDBI {
    override suspend fun create(userData: UserCreateData) {
        dbQuery {
            UserEntity.new(UUID.fromString(userData.id)) {
                fromCreateData(userData)
            }
        }
    }

    override suspend fun get(id: DtId<UserData>): UserEntity? =
        dbQuery {
            UserEntity.findById(id.id)
        }

    override suspend fun get(email: String): UserEntity? =
        dbQuery {
            UserEntity
                .find { UsersTable.email eq email }
                .limit(1)
                .firstOrNull()
        }

    override suspend fun verifyEmail(id: DtId<UserData>) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[email_verified] = true
            }
        }
    }

    override suspend fun changePassword(id: DtId<UserData>, newPasswordHashed: String) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[password_hash] = newPasswordHashed
            }
        }
    }

    override suspend fun resetPassword(
        id: DtId<UserData>,
        newPasswordHashed: String,
        verifyEmail: Boolean,
    ) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                if (verifyEmail) {
                    it[email_verified] = true
                }
                it[password_hash] = newPasswordHashed
            }
        }
    }

    override suspend fun delete(id: DtId<UserData>) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id.toEntityId(UsersTable) }
        }
    }
}