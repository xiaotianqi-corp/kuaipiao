package org.xiaotianqi.kuaipiao.data.sources.db.schemas.user

import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable.created_at
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable.expires_at
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable.id
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable.token
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable.user
import org.xiaotianqi.kuaipiao.data.sources.db.toDtId
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.domain.auth.UserData
import org.xiaotianqi.kuaipiao.domain.password.PasswordResetData
import java.time.Instant
import java.util.UUID

/**
 * @property id
 * @property token
 * @property user
 * @property created_at
 * @property expires_at
 */
object PasswordResetTable : IntIdTable() {
    val token = varchar("token", 100).uniqueIndex()
    val user =
        reference(
            name = "id_user",
            foreign = UsersTable,
            onDelete = ReferenceOption.CASCADE,
        ).index()
    val created_at = timestamp("created_at")
    val expires_at = timestamp("expires_at")
}

/**
 * @property id
 * @property token
 * @property user
 * @property created_at
 * @property expires_at
 * @property userEntity
 */
class PasswordResetEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PasswordResetEntity>(PasswordResetTable)

    var token by PasswordResetTable.token
    var user by PasswordResetTable.user
    var created_at by PasswordResetTable.created_at
    var expires_at by PasswordResetTable.expires_at

    val userEntity by UserEntity referencedOn PasswordResetTable.user
}

fun PasswordResetEntity.fromData(passwordResetData: PasswordResetData) {
    val userIdUuid = UUID.fromString(passwordResetData.userId)

    token = passwordResetData.token
    user = DtId<UserData>(userIdUuid).toEntityId(UsersTable)
    created_at = Instant.ofEpochMilli(passwordResetData.createdAt)
    expires_at = Instant.ofEpochMilli(passwordResetData.expireAt)
}

fun PasswordResetEntity.toData() =
    PasswordResetData(
        token = token,
        userId = user.toDtId<UserData>().id.toString(),
        createdAt = created_at.toEpochMilli(),
        expireAt = expires_at.toEpochMilli(),
    )
