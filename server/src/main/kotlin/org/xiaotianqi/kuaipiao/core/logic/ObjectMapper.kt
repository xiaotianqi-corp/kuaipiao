package org.xiaotianqi.kuaipiao.core.logic

import org.xiaotianqi.kuaipiao.core.logic.typedId.serialization.IdKotlinXSerializationModule
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import kotlin.reflect.KClass

@Factory
class ObjectMapper {
    val json = Json {
        serializersModule = IdKotlinXSerializationModule
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> encode(data: T): String {
        return json.encodeToString(data)
    }

    inline fun <reified T : Any> decode(serializedData: String, clazz: KClass<T>): T {
        return json.decodeFromString(serializedData)
    }
}
