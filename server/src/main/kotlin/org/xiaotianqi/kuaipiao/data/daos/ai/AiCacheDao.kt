package org.xiaotianqi.kuaipiao.data.daos.ai

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.AiCacheTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.AiCacheTable.aiProvider
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.AiCacheTable.cacheValue
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.AiCacheTable.expiresAt
import org.xiaotianqi.kuaipiao.domain.cache.CacheEntry
import org.xiaotianqi.kuaipiao.domain.cache.CacheStats
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant
import kotlin.collections.sumOf

@Single
@ExperimentalTime
@ExperimentalStdlibApi
class AiCacheDao {

    fun saveCache(
        key: String,
        value: String,
        cacheType: String,
        aiProvider: String,
        operation: String,
        confidence: Double? = null,
        processingTime: Long,
        ttlHours: Int = 24
    ) {
        transaction {
            AiCacheTable.deleteWhere { AiCacheTable.cacheKey eq key }

            AiCacheTable.insert {
                it[cacheKey] = key
                it[cacheValue] = value
                it[this.cacheType] = cacheType
                it[this.aiProvider] = aiProvider
                it[this.operation] = operation
                it[this.confidence] = confidence
                it[this.processingTime] = processingTime
                it[expiresAt] = java.time.Instant.now().plusSeconds(ttlHours * 3600L)
                it[createdAt] = java.time.Instant.now()
            }
        }
    }

    suspend fun getCache(key: String): CacheEntry? = transaction {
        AiCacheTable
            .selectAll()
            .where { (AiCacheTable.cacheKey eq key) and (AiCacheTable.expiresAt greater Instant.now()) }
            .firstOrNull()
            ?.let {
                CacheEntry(
                    key = it[AiCacheTable.cacheKey],
                    value = it[AiCacheTable.cacheValue],
                    type = it[AiCacheTable.cacheType],
                    aiProvider = it[AiCacheTable.aiProvider],
                    operation = it[AiCacheTable.operation],
                    confidence = it[AiCacheTable.confidence],
                    processingTime = it[AiCacheTable.processingTime],
                    expiresAt = it[AiCacheTable.expiresAt].toKotlinInstant(),
                    createdAt = it[AiCacheTable.createdAt].toKotlinInstant()
                )
            }
    }

    fun invalidateCache(key: String): Boolean {
        return transaction {
            AiCacheTable.deleteWhere { AiCacheTable.cacheKey eq key } > 0
        }
    }

    fun invalidateByPattern(pattern: String): Int {
        return transaction {
            AiCacheTable.deleteWhere { cacheKey like "%$pattern%" }
        }
    }

    fun cleanupExpired(): Int {
        return transaction {
            AiCacheTable.deleteWhere { expiresAt less java.time.Instant.now() }
        }
    }

    suspend fun getCacheStats(): CacheStats = transaction {
        val total = AiCacheTable.selectAll().count()
        val expired = AiCacheTable.selectAll().where { expiresAt less Instant.now() }.count()
        val byProvider = AiCacheTable
            .selectAll()
            .groupBy { it[aiProvider] }
            .mapValues { it.value.size.toLong() }

        CacheStats(
            totalEntries = total,
            expiredEntries = expired,
            entriesByProvider = byProvider,
            cacheSize = AiCacheTable.selectAll().sumOf { it[cacheValue].length.toLong() }
        )
    }
}

