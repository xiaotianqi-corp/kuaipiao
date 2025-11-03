package org.xiaotianqi.kuaipiao.data.sources.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.*

fun <T> EntityID<UUID>.toDtId(): DtId<T> = DtId(value)

fun DtId<*>.toEntityId(table: IdTable<UUID>) = EntityID(this.id, table)