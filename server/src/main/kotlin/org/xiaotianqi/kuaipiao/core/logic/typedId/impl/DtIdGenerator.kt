package org.xiaotianqi.kuaipiao.core.logic.typedId.impl

import org.xiaotianqi.kuaipiao.core.logic.typedId.Id
import org.xiaotianqi.kuaipiao.core.logic.typedId.IdGenerator
import org.koin.core.annotation.Factory
import java.util.*
import kotlin.reflect.KClass

/**
 * Generator of [DtId] based on [UUID].
 */
@Factory
class DtIdGenerator : IdGenerator {
    override val idClass: KClass<out Id<*>> = DtId::class

    override val wrappedIdClass: KClass<out Any> = UUID::class

    override fun <T> generateNewId(): Id<T> = DtId(UUID.randomUUID())
}
