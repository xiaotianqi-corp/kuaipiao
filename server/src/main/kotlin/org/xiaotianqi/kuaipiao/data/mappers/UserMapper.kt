package org.xiaotianqi.kuaipiao.data.mappers

import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.auth.UserCreateData
import org.xiaotianqi.kuaipiao.domain.auth.UserResponse
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserEntity
import java.time.Instant
import java.util.UUID

fun UserEntity.toDomain() = UserData(
    id = id.value.toString(),
    email = email,
    passwordHash = passwordHash,
    emailVerified = emailVerified,
    creationTimestamp = createdAt.toEpochMilli()
)

fun UserEntity.toResponse() = UserResponse(
    id = id.value.toString(),
    email = email,
    creationTimestamp = createdAt.toEpochMilli()
)

fun UserData.toResponse() = UserResponse(
    id = id,
    email = email,
    creationTimestamp = creationTimestamp
)

fun UserEntity.fromCreateData(data: UserCreateData) {
    email = data.email
    passwordHash = data.passwordHash
    emailVerified = data.emailVerified
    createdAt = Instant.ofEpochMilli(data.creationTimestamp)
}