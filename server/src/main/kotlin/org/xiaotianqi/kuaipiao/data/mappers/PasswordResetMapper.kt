package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.util.UUID

// Entity -> Domain
fun PasswordResetEntity.toDomain() = PasswordResetData(
    token = token,
    userId = user.value.toString(),
    expireAt = expires_at.toEpochMilli(),
    createdAt = created_at.toEpochMilli()
)

// Domain -> Entity (para new())
fun PasswordResetEntity.fromDomain(data: PasswordResetData) {
    token = data.token
    user = EntityID(UUID.fromString(data.userId), UsersTable)
    expires_at = Instant.ofEpochMilli(data.expireAt)
    created_at = Instant.ofEpochMilli(data.createdAt)
}