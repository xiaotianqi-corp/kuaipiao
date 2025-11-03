package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.email.EmailVerificationData
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.EmailVerificationEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.util.UUID

fun EmailVerificationEntity.toDomain() = EmailVerificationData(
    token = token,
    userId = user.value.toString(),
    expireAt = expires_at.toEpochMilli(),
    createdAt = created_at.toEpochMilli()
)

fun EmailVerificationEntity.fromDomain(data: EmailVerificationData) {
    token = data.token
    user = EntityID(UUID.fromString(data.userId), UsersTable)
    expires_at = Instant.ofEpochMilli(data.expireAt)
    created_at = Instant.ofEpochMilli(data.createdAt)
}