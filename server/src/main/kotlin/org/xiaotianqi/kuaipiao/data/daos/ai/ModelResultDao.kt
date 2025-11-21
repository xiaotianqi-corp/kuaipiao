package org.xiaotianqi.kuaipiao.data.daos.ai

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.ModelResultsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.ModelResultEntity
import org.xiaotianqi.kuaipiao.domain.models.*
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

@Single
@ExperimentalTime
class ModelResultDao {

    suspend fun saveModelResult(
        modelType: String,
        aiProvider: String,
        operation: String,
        inputHash: String,
        inputData: String?,
        outputData: String,
        confidence: Double,
        processingTime: Long,
        tokensUsed: Int? = null,
        cost: Double? = null,
        success: Boolean = true,
        errorMessage: String? = null
    ): String = transaction {
        val entity = ModelResultEntity.new {
            this.modelType = modelType
            this.aiProvider = aiProvider
            this.operation = operation
            this.inputHash = inputHash
            this.inputData = inputData
            this.outputData = outputData
            this.confidence = confidence
            this.processingTime = processingTime
            this.tokensUsed = tokensUsed
            this.cost = cost?.toBigDecimal()
            this.success = success
            this.errorMessage = errorMessage
            createdAt = Instant.now()
        }
        entity.id.value.toString()
    }

    suspend fun findSimilarResults(
        inputHash: String,
        operation: String,
        similarityThreshold: Double = 0.8
    ): List<ModelResult> = transaction {
        ModelResultsTable
            .selectAll()
            .where {
                (ModelResultsTable.inputHash eq inputHash) and
                        (ModelResultsTable.operation eq operation) and
                        (ModelResultsTable.success eq true) and
                        (ModelResultsTable.confidence greaterEq similarityThreshold)
            }
            .orderBy(ModelResultsTable.confidence to SortOrder.DESC)
            .limit(5)
            .map { rowToModelResult(it) }
    }

    suspend fun getProviderStats(
        startDate: Instant,
        endDate: Instant
    ): List<ProviderStats> = transaction {
        ModelResultsTable
            .select(
                ModelResultsTable.aiProvider,
                ModelResultsTable.id.count(),
                ModelResultsTable.confidence.avg(),
                ModelResultsTable.processingTime.avg(),
                ModelResultsTable.tokensUsed.sum(),
                ModelResultsTable.cost.sum()
            )
            .where { ModelResultsTable.createdAt.between(startDate, endDate) }
            .groupBy(ModelResultsTable.aiProvider)
            .map { row ->
                val avgConf = row[ModelResultsTable.confidence.avg()]?.let { (it as? Number)?.toDouble() } ?: 0.0
                val avgTime = row[ModelResultsTable.processingTime.avg()]?.let { (it as? Number)?.toLong() } ?: 0L
                val tokens = row[ModelResultsTable.tokensUsed.sum()]?.let { (it as? Number)?.toInt() } ?: 0
                val costVal = row[ModelResultsTable.cost.sum()]?.toDouble() ?: 0.0

                ProviderStats(
                    provider = row[ModelResultsTable.aiProvider],
                    totalRequests = row[ModelResultsTable.id.count()].toInt(),
                    averageConfidence = avgConf,
                    averageProcessingTime = avgTime,
                    totalTokens = tokens,
                    totalCost = costVal
                )
            }
    }

    suspend fun getOperationStats(
        provider: String,
        days: Int = 30
    ): List<OperationStats> = transaction {
        val startDate = Instant.now().minusSeconds(days * 86400L)

        ModelResultsTable
            .select(
                ModelResultsTable.operation,
                ModelResultsTable.id.count(),
                ModelResultsTable.confidence.avg(),
                ModelResultsTable.processingTime.avg(),
                ModelResultsTable.success.count()
            )
            .where {
                (ModelResultsTable.aiProvider eq provider) and
                        (ModelResultsTable.createdAt greater startDate)
            }
            .groupBy(ModelResultsTable.operation)
            .map { row ->
                val total = row[ModelResultsTable.id.count()]
                val avgConf = row[ModelResultsTable.confidence.avg()]?.let { (it as? Number)?.toDouble() } ?: 0.0
                val avgTime = row[ModelResultsTable.processingTime.avg()]?.let { (it as? Number)?.toLong() } ?: 0L

                OperationStats(
                    operation = row[ModelResultsTable.operation],
                    totalRequests = total.toInt(),
                    averageConfidence = avgConf,
                    averageProcessingTime = avgTime,
                    successRate = row[ModelResultsTable.success.count()].toDouble() / total
                )
            }
    }

    suspend fun getCostAnalysis(
        startDate: Instant,
        endDate: Instant
    ): CostAnalysis = transaction {
        val totalCostResult = ModelResultsTable
            .select(ModelResultsTable.cost.sum())
            .where { ModelResultsTable.createdAt.between(startDate, endDate) }
            .firstOrNull()
        val totalCost = totalCostResult?.get(ModelResultsTable.cost.sum())?.toDouble() ?: 0.0

        val costByProvider = ModelResultsTable
            .select(ModelResultsTable.aiProvider, ModelResultsTable.cost.sum())
            .where { ModelResultsTable.createdAt.between(startDate, endDate) }
            .groupBy(ModelResultsTable.aiProvider)
            .associate { it[ModelResultsTable.aiProvider] to (it[ModelResultsTable.cost.sum()]?.toDouble() ?: 0.0) }

        val costByOperation = ModelResultsTable
            .select(ModelResultsTable.operation, ModelResultsTable.cost.sum())
            .where { ModelResultsTable.createdAt.between(startDate, endDate) }
            .groupBy(ModelResultsTable.operation)
            .associate { it[ModelResultsTable.operation] to (it[ModelResultsTable.cost.sum()]?.toDouble() ?: 0.0) }

        CostAnalysis(
            totalCost = totalCost,
            costByProvider = costByProvider,
            costByOperation = costByOperation,
            period = startDate.toKotlinInstant() to endDate.toKotlinInstant()
        )
    }

    private fun rowToModelResult(row: ResultRow): ModelResult {
        return ModelResult(
            modelType = row[ModelResultsTable.modelType],
            aiProvider = row[ModelResultsTable.aiProvider],
            operation = row[ModelResultsTable.operation],
            inputHash = row[ModelResultsTable.inputHash],
            inputData = row[ModelResultsTable.inputData],
            outputData = row[ModelResultsTable.outputData],
            confidence = row[ModelResultsTable.confidence],
            processingTime = row[ModelResultsTable.processingTime],
            tokensUsed = row[ModelResultsTable.tokensUsed],
            cost = row[ModelResultsTable.cost]?.toDouble(),
            success = row[ModelResultsTable.success],
            errorMessage = row[ModelResultsTable.errorMessage],
            createdAt = row[ModelResultsTable.createdAt].toKotlinInstant()
        )
    }
}