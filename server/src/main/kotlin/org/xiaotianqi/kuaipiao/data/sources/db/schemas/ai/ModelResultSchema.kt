package org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object ModelResultsTable : UUIDTable("ai_model_results") {
    val modelType = varchar("model_type", 50)
    val aiProvider = varchar("ai_provider", 50)
    val operation = varchar("operation", 100)
    val inputHash = varchar("input_hash", 64).index()
    val inputData = text("input_data").nullable()
    val outputData = text("output_data")
    val confidence = double("confidence")
    val processingTime = long("processing_time")
    val tokensUsed = integer("tokens_used").nullable()
    val cost = decimal("cost", 10, 6).nullable()
    val success = bool("success")
    val errorMessage = text("error_message").nullable()
    val createdAt = timestamp("created_at")
}

class ModelResultEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ModelResultEntity>(ModelResultsTable)
    var modelType by ModelResultsTable.modelType
    var aiProvider by ModelResultsTable.aiProvider
    var operation by ModelResultsTable.operation
    var inputHash by ModelResultsTable.inputHash
    var inputData by ModelResultsTable.inputData
    var outputData by ModelResultsTable.outputData
    var confidence by ModelResultsTable.confidence
    var processingTime by ModelResultsTable.processingTime
    var tokensUsed by ModelResultsTable.tokensUsed
    var cost by ModelResultsTable.cost
    var success by ModelResultsTable.success
    var errorMessage by ModelResultsTable.errorMessage
    var createdAt by ModelResultsTable.createdAt
}