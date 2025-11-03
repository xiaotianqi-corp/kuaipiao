package org.xiaotianqi.kuaipiao.utils

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import java.util.UUID

// String -> DtId
fun <T> String.toDtId(): DtId<T> = DtId(UUID.fromString(this))

// DtId -> String
fun <T> DtId<T>.toStringId(): String = this.id.toString()