package org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
suspend inline fun <reified T> AiCacheSource.get(
    key: String,
    operation: String = "unknown"
): T? {
    return this.get(key, T::class.java, operation)
}
