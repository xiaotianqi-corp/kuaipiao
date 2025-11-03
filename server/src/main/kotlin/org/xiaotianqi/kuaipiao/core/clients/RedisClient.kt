package org.xiaotianqi.kuaipiao.core.clients

import io.lettuce.core.RedisClient
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.config.RedisConfig
import org.xiaotianqi.kuaipiao.di.IClosableComponent

@OptIn(ExperimentalLettuceCoroutinesApi::class)
@Single(createdAtStart = true)
class RedisClient : IClosableComponent {

    private val client = RedisClient.create(RedisConfig.connectionString)
    private val connection = client.connect()
    val commands: RedisCoroutinesCommands<String, String> = connection.coroutines()

    override suspend fun close() {
        try {
            connection.close()
        } finally {
            client.shutdown()
        }
    }
}
