package org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.impl

import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.user.UserDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import org.xiaotianqi.kuaipiao.data.mappers.toDomain
import kotlin.time.toKotlinInstant

@Single(createdAtStart = true)
@ExperimentalTime
class UserDBIImpl : UserDBI {

    override suspend fun create(data: UserCreateData) = dbQuery {
        UserEntity.new(UUID.fromString(data.id)) {
            username = data.username
            firstName = data.firstName
            lastName = data.lastName
            email = data.email
            emailVerified = data.emailVerified
            passwordHash = data.passwordHash
            enterprise = null
            isActive = true
            createdAt = data.createdAt.toJavaInstant()
            updatedAt = null
            lastLoginAt = null
        }
    }

    override suspend fun createAndReturnEntity(data: UserCreateData): UserEntity = dbQuery {
        UserEntity.new(UUID.fromString(data.id)) {
            username = data.username
            firstName = data.firstName
            lastName = data.lastName
            email = data.email
            emailVerified = data.emailVerified
            passwordHash = data.passwordHash
            enterprise = null
            isActive = true
            createdAt = data.createdAt.toJavaInstant()
            updatedAt = null
            lastLoginAt = null
        }
    }

    override suspend fun get(id: String): UserEntity? = dbQuery {
        UserEntity.find { UsersTable.email eq id }.limit(1).firstOrNull()
    }

    override suspend fun getAll(page: Int, limit: Int): List<UserEntity> = dbQuery {
        UserEntity.all().limit(limit).offset(start = (page * limit).toLong()).toList()
    }

    override suspend fun updateStatus(id: DtId<UserData>, isActive: Boolean) = dbQuery {
        UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
            it[UsersTable.isActive] = isActive
        }
    }

    override suspend fun getByEmail(email: String): UserData? = dbQuery {
        UserEntity.find { UsersTable.email eq email }
            .with(UserEntity::enterprise, UserEntity::organizations, UserEntity::roles)
            .limit(1)
            .firstOrNull()
            ?.toDomain()
    }

    override suspend fun verifyEmail(id: DtId<UserData>) = dbQuery {
        UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
            it[UsersTable.emailVerified] = true
        }
    }

    override suspend fun resetPassword(id: DtId<UserData>, newPasswordHashed: String, verifyEmail: Boolean) = dbQuery {
        UsersTable.update({ UsersTable.id eq id.toEntityId(UsersTable) }) {
            it[UsersTable.passwordHash] = newPasswordHashed
            if (verifyEmail) it[UsersTable.emailVerified] = true
        }
    }

    override suspend fun delete(id: DtId<UserData>) = dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id.toEntityId(UsersTable) }
    }
}