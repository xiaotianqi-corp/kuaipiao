package org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.core.clients.RedisClient
import org.xiaotianqi.kuaipiao.data.daos.ai.AiCacheDao
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.xiaotianqi.kuaipiao.domain.cache.CacheStats
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.trade.TariffClassification
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}
private val json = Json { ignoreUnknownKeys = true }

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class AiCacheSource(
    private val aiCacheDao: AiCacheDao,
    private val redisClient: RedisClient
) {
    private val redisTtlSeconds = 3600L // 1 hora

    suspend fun <T> get(
        key: String,
        clazz: Class<T>,
        operation: String = "unknown"
    ): T? {
        return try {
            val cacheKey = generateCacheKey(key, operation)

            // L1: Redis
            val redisValue = redisClient.commands.get(cacheKey)
            if (redisValue != null) {
                logger.debug { "Redis hit: $cacheKey" }
                return deserializeValue(redisValue, clazz)
            }

            // L2: PostgreSQL
            val cacheEntry = aiCacheDao.getCache(cacheKey)
            cacheEntry?.let { entry ->
                logger.debug { "DB hit: ${entry.key}" }
                val value = deserializeValue(entry.value, clazz)

                // Repoblar Redis
                value?.let {
                    redisClient.commands.setex(cacheKey, redisTtlSeconds, entry.value)
                }

                value
            }
        } catch (e: Exception) {
            logger.warn(e) { "Cache read error: $key" }
            null
        }
    }

    suspend fun <T : Any> set(
        key: String,
        value: T,
        operation: String = "unknown",
        aiProvider: String = "unknown",
        confidence: Double? = null,
        processingTime: Long = 0,
        ttlHours: Int = 24
    ) {
        try {
            val serializedValue = serializeValue(value)
            val cacheKey = generateCacheKey(key, operation)

            // L1: Redis
            redisClient.commands.setex(cacheKey, redisTtlSeconds, serializedValue)

            // L2: PostgreSQL
            aiCacheDao.saveCache(
                key = cacheKey,
                value = serializedValue,
                cacheType = value::class.simpleName ?: "unknown",
                aiProvider = aiProvider,
                operation = operation,
                confidence = confidence,
                processingTime = processingTime,
                ttlHours = ttlHours
            )

            logger.debug { "Cache set: $cacheKey" }
        } catch (e: Exception) {
            logger.warn(e) { "Cache write error: $key" }
        }
    }

    suspend fun invalidate(key: String, operation: String = "unknown"): Boolean {
        return try {
            val cacheKey = generateCacheKey(key, operation)
            redisClient.commands.del(cacheKey)
            aiCacheDao.invalidateCache(cacheKey)
        } catch (e: Exception) {
            logger.warn(e) { "Cache invalidation error: $key" }
            false
        }
    }

    suspend fun invalidateByPattern(pattern: String): Int {
        return try {
            val keys = mutableListOf<String>()
            var cursor = "0"

            do {
                val scanResult = redisClient.commands.scan(
                    io.lettuce.core.ScanCursor.of(cursor),
                    io.lettuce.core.ScanArgs.Builder.matches("*$pattern*")
                ) ?: break

                keys.addAll(scanResult.keys)
                cursor = scanResult.cursor
            } while (cursor != "0")

            if (keys.isNotEmpty()) {
                redisClient.commands.del(*keys.toTypedArray())
            }

            aiCacheDao.invalidateByPattern(pattern)
        } catch (e: Exception) {
            logger.warn(e) { "Pattern invalidation error: $pattern" }
            0
        }
    }

    suspend fun cleanupExpired(): Int {
        return try {
            aiCacheDao.cleanupExpired()
        } catch (e: Exception) {
            logger.warn(e) { "Cleanup error" }
            0
        }
    }

    suspend fun getStats(): CacheStats {
        return try {
            val dbStats = aiCacheDao.getCacheStats()
            val redisInfo = redisClient.commands.info("stats")

            dbStats.copy(
                additionalInfo = mapOf(
                    "redis_connected" to "true",
                    "redis_stats" to (redisInfo ?: "unavailable")
                )
            )
        } catch (e: Exception) {
            logger.warn(e) { "Stats error" }
            CacheStats(
                totalEntries = 0,
                expiredEntries = 0,
                entriesByProvider = emptyMap(),
                cacheSize = 0
            )
        }
    }

    suspend fun getInvoiceProcessing(
        fileHash: String,
        country: String
    ): InvoiceProcessingResult? {
        return get(
            key = "invoice_${fileHash}_$country",
            clazz = InvoiceProcessingResult::class.java,
            operation = "invoice_processing"
        )
    }

    suspend fun setInvoiceProcessing(
        fileHash: String,
        country: String,
        result: InvoiceProcessingResult,
        aiProvider: String,
        processingTime: Long
    ) {
        set(
            key = "invoice_${fileHash}_$country",
            value = result,
            operation = "invoice_processing",
            aiProvider = aiProvider,
            confidence = result.confidence,
            processingTime = processingTime
        )
    }

    suspend fun getTariffClassification(
        productHash: String,
        origin: String,
        destination: String
    ): TariffClassification? {
        return get(
            key = "tariff_${productHash}_${origin}_$destination",
            clazz = TariffClassification::class.java,
            operation = "tariff_classification"
        )
    }

    suspend fun setTariffClassification(
        productHash: String,
        origin: String,
        destination: String,
        classification: TariffClassification,
        aiProvider: String,
        processingTime: Long
    ) {
        set(
            key = "tariff_${productHash}_${origin}_$destination",
            value = classification,
            operation = "tariff_classification",
            aiProvider = aiProvider,
            confidence = classification.confidence,
            processingTime = processingTime
        )
    }

    private fun generateCacheKey(key: String, operation: String): String {
        return "${operation}_${key.hashCode()}"
    }

    private fun serializeValue(value: Any): String {
        return when (value) {
            is String -> value
            else -> {
                val serializer = json.serializersModule.serializer(value::class.java)
                json.encodeToString(serializer, value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> deserializeValue(value: String, clazz: Class<T>): T? {
        return try {
            when {
                clazz == String::class.java -> value as T
                else -> json.decodeFromString(
                    json.serializersModule.serializer(clazz),
                    value
                ) as T
            }
        } catch (e: Exception) {
            logger.warn(e) { "Deserialization error: ${clazz.simpleName}" }
            null
        }
    }
}