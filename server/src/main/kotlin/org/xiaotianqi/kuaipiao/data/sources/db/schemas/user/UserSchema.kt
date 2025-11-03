package org.xiaotianqi.kuaipiao.data.sources.db.schemas.user

import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.data.sources.db.toDtId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

object UsersTable : UUIDTable("users") {
    val email = varchar("email", 150).uniqueIndex()
    val password_hash = varchar("password_hash", 100)
    val email_verified = bool("email_verified")
    val created_at = timestamp("created_at")
}

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var passwordHash by UsersTable.password_hash
    var emailVerified by UsersTable.email_verified
    var createdAt by UsersTable.created_at
}

fun UserEntity.fromData(userData: UserData) {
    email = userData.email
    passwordHash = userData.passwordHash
    emailVerified = userData.emailVerified
    createdAt = Instant.ofEpochMilli(userData.creationTimestamp)
}

fun UserEntity.toData() =
    UserData(
        id = id.value.toString(),
        email = email,
        passwordHash = passwordHash,
        emailVerified = emailVerified,
        creationTimestamp = createdAt.toEpochMilli(),
    )
