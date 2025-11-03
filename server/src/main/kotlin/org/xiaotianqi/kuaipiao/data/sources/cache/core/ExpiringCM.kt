package org.xiaotianqi.kuaipiao.data.sources.cache.core

import org.xiaotianqi.kuaipiao.core.clients.RedisClient
import org.xiaotianqi.kuaipiao.core.logic.ObjectMapper
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

@OptIn(ExperimentalLettuceCoroutinesApi::class)
abstract class ExpiringCM(
    protected val keyBase: String,
    protected val expirationInSeconds: Long,
    protected val redisClient: RedisClient,
    protected val objectMapper: ObjectMapper,
) {

    protected val commands: RedisCoroutinesCommands<String, String> = redisClient.commands

    protected fun keyName(hashValue: String) = "$keyBase:$hashValue"

    protected suspend inline fun <reified T : Any> get(keyValue: String): T? {
        val json = commands.get(keyName(keyValue)) ?: return null
        return objectMapper.decode(json, T::class)
    }

    protected inline fun <reified T : Any> cache(keyValue: String, data: T) {
        val json = objectMapper.encode(data)
        runBlocking {
            commands.setex(keyName(keyValue), expirationInSeconds, json)
        }
    }

    protected suspend fun delete(keyValue: String) {
        commands.del(keyName(keyValue))
    }
}
