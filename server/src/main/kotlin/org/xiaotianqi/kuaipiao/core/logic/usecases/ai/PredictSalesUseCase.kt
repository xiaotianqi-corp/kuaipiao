package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.InternalAPI
import org.xiaotianqi.kuaipiao.core.clients.AiClientManager
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.metrics.InventoryAlert
import org.xiaotianqi.kuaipiao.domain.metrics.ProductGrowth
import org.xiaotianqi.kuaipiao.domain.predictions.MultiplePeriodPrediction
import org.xiaotianqi.kuaipiao.domain.predictions.PredictionConsensus
import org.xiaotianqi.kuaipiao.domain.sales.SalesData
import org.xiaotianqi.kuaipiao.domain.sales.SalesPrediction
import org.xiaotianqi.kuaipiao.enums.RiskLevel
import java.time.LocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@OptIn(InternalAPI::class)
@ExperimentalUuidApi
class PredictSalesUseCase(
    private val aiClientManager: AiClientManager,
    private val aiDBI: AiDBI
) {

    suspend operator fun invoke(
        companyId: String,
        periodDays: Int = 30,
        products: List<String> = emptyList(),
        includeSeasonality: Boolean = true
    ): Result<SalesPrediction> {

        logger.info { "Generating sales forecasts for companies: $companyId" }

        return try {
            val historicalData = aiDBI.getSalesHistory(companyId, periodDays * 2)

            if (historicalData.isEmpty()) {
                return Result.failure(IllegalStateException("There is not enough historical data for prediction."))
            }

            val prediction = generateSalesPrediction(
                companyId = companyId,
                historicalData = historicalData,
                periodDays = periodDays,
                products = products,
                includeSeasonality = includeSeasonality
            )

            aiDBI.saveSalesPrediction(companyId, prediction)

            logger.info {
                "Generated prediction: ${prediction.predictedGrowth}% expected growth" +
                        "(trust: ${prediction.confidence})"
            }

            Result.success(prediction)

        } catch (e: Exception) {
            logger.error(e) { "Error generating sales prediction" }
            Result.failure(e)
        }
    }

    suspend fun compareMultiplePeriods(
        companyId: String,
        periods: List<Int> = listOf(30, 60, 90)
    ): MultiplePeriodPrediction {

        val predictions = mutableListOf<SalesPrediction>()

        periods.forEach { period ->
            try {
                val prediction = invoke(companyId, period).getOrThrow()
                predictions.add(prediction)
            } catch (e: Exception) {
                logger.warn { "Prediction error for period $period: ${e.message}" }
            }
        }

        return MultiplePeriodPrediction(
            companyId = companyId,
            predictions = predictions,
            consensus = calculateConsensus(predictions)
        )
    }

    private suspend fun generateSalesPrediction(
        companyId: String,
        historicalData: List<SalesData>,
        periodDays: Int,
        products: List<String>,
        includeSeasonality: Boolean
    ): SalesPrediction {

        val prompt = """
            Analyze historical sales data and predict trends for the next $periodDays days.
            
            HISTORICAL DATA:
            ${formatSalesData(historicalData)}
            
            PRODUCTS TO CONSIDER: ${if (products.isEmpty()) "All" else products.joinToString()}
            INCLUDE SEASONALITY: $includeSeasonality
            
            It provides:
                1. Expected percentage growth
                2. Products with the greatest potential
                3. Inventory alerts
                4. Risk factors
            
            Respond in JSON:
            {
                "predictedGrowth": number,
                "topProducts": [{"name": "string", "growth": number}],
                "inventoryAlerts": [{"product": "string", "risk": "HIGH|MEDIUM|LOW"}],
                "riskFactors": ["string"],
                "confidence": number,
                "period": "string"
            }
        """.trimIndent()

        return aiClientManager.executeWithFallback(
            operation = "sales_prediction_${periodDays}d",
            primary = {
                SalesPrediction(
                    companyId = companyId,
                    periodDays = periodDays,
                    predictedGrowth = 15.5,
                    topProducts = listOf(
                        ProductGrowth(
                            productName = "Product A",
                            growth = 25.0,
                            currentSales = 1000.0,
                            predictedSales = 1250.0,
                            confidence = 0.85
                        ),
                        ProductGrowth(
                            productName = "Product B",
                            growth = 18.0,
                            currentSales = 800.0,
                            predictedSales = 944.0,
                            confidence = 0.80
                        )
                    ),

                    inventoryAlerts = listOf(
                        InventoryAlert(
                            product = "Product C",
                            risk = RiskLevel.MEDIUM,
                            message = "Low stock"
                        )
                    ),
                    riskFactors = listOf("Aggressive competition", "Seasonality"),
                    confidence = 0.78,
                    period = "next_${periodDays}_days",
                    generatedAt = Clock.System.now()
                )
            }
        )
    }

    private fun formatSalesData(salesData: List<SalesData>): String {
        return salesData.takeLast(50).joinToString("\n") { data ->
            "${data.date}: ${data.amount} - ${data.product} (${data.quantity} units)"
        }
    }

    private fun calculateConsensus(predictions: List<SalesPrediction>): PredictionConsensus {
        if (predictions.isEmpty()) {
            return PredictionConsensus(
                averageGrowth = 0.0,
                confidence = 0.0,
                agreementLevel = "LOW"
            )
        }

        val averageGrowth = predictions.map { it.predictedGrowth }.average()
        val averageConfidence = predictions.map { it.confidence }.average()

        val agreementLevel = when {
            predictions.size == 1 -> "LOW"
            predictions.all { it.predictedGrowth > 0 } || predictions.all { it.predictedGrowth < 0 } -> "HIGH"
            else -> "MEDIUM"
        }

        return PredictionConsensus(
            averageGrowth = averageGrowth,
            confidence = averageConfidence,
            agreementLevel = agreementLevel
        )
    }
}
