package org.xiaotianqi.kuaipiao.core.logic.typedId

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtIntId
import java.util.*

fun <T> newDtId() = DtId<T>(UUID.randomUUID())

fun <T> newDtIntId() = DtIntId<T>((1..100).random())
