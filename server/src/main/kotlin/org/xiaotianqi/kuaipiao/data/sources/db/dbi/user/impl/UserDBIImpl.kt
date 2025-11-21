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
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseEntity
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.domain.enterprise.EnterpriseCreateData
import org.xiaotianqi.kuaipiao.enums.EnterprisePlan
import org.xiaotianqi.kuaipiao.enums.EntityStatus
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class UserDBIImpl : UserDBI {
    override suspend fun create(userData: UserCreateData) {
        dbQuery {
            val enterpriseEntity = EnterpriseEntity.new(UUID.randomUUID()) {
                fromCreateData(
                    EnterpriseCreateData(
                        id = UUID.randomUUID().toString(),
                        subdomain = userData.email.substringBefore("@"),
                        domain = null,
                        status = EntityStatus.ACTIVE,
                        plan = EnterprisePlan.FREE,
                        settings = "",
                        metadata = "",
                        createdAt = Clock.System.now()
                    )
                )
            }

            UserEntity.new(UUID.fromString(userData.id)) {
                fromCreateData(userData, enterpriseEntity)
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

    override suspend fun getAll(page: Int, limit: Int): List<UserEntity> = dbQuery {
        UserEntity.all()
            .limit(limit).offset(start = (page * limit).toLong())
            .toList()
    }

    override suspend fun updateStatus(id: DtId<UserData>, isActive: Boolean) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[UsersTable.isActive] = isActive
            }
        }
    }

    override suspend fun verifyEmail(id: DtId<UserData>) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[emailVerified] = true
            }
        }
    }

    override suspend fun changePassword(id: DtId<UserData>, newPasswordHashed: String) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
                it[passwordHash] = newPasswordHashed
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
                    it[emailVerified] = true
                }
                it[passwordHash] = newPasswordHashed
            }
        }
    }

    override suspend fun delete(id: DtId<UserData>) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id.toEntityId(UsersTable) }
        }
    }
}