package org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object AiCacheTable : UUIDTable("ai_cache") {
    val cacheKey = varchar("cache_key", 255).uniqueIndex()
    val cacheValue = text("cache_value")
    val cacheType = varchar("cache_type", 50)
    val aiProvider = varchar("ai_provider", 50)
    val operation = varchar("operation", 100)
    val confidence = double("confidence").nullable()
    val processingTime = long("processing_time")
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")
}

class AiCacheEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AiCacheEntity>(AiCacheTable)
    var cacheKey by AiCacheTable.cacheKey
    var cacheValue by AiCacheTable.cacheValue
    var cacheType by AiCacheTable.cacheType
    var aiProvider by AiCacheTable.aiProvider
    var operation by AiCacheTable.operation
    var confidence by AiCacheTable.confidence
    var processingTime by AiCacheTable.processingTime
    var expiresAt by AiCacheTable.expiresAt
    var createdAt by AiCacheTable.createdAt
}