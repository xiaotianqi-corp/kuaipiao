package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.xiaotianqi.kuaipiao.core.logic.ai.AiOrchestrator
import org.xiaotianqi.kuaipiao.core.logic.ai.TaxComplianceAnalyzer
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.risk.HighRiskCompany
import org.xiaotianqi.kuaipiao.domain.risk.IndustryRiskAnalysis
import org.xiaotianqi.kuaipiao.enums.RiskComparison
import org.xiaotianqi.kuaipiao.enums.RiskPatternType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class AnalyzeComplianceRiskUseCase(
    private val aiOrchestrator: AiOrchestrator,
    private val taxComplianceAnalyzer: TaxComplianceAnalyzer,
    private val aiDBI: AiDBI
) {

    suspend operator fun invoke(
        companyId: String,
        period: DateRange? = null,
        transactionTypes: List<String> = emptyList()
    ): Result<ComplianceRiskAnalysis> {

        logger.info { "Analyzing compliance risk for a company: $companyId" }

        return try {
            val analysisPeriod = period ?: DateRange.last90Days()

            val transactions = aiDBI.getCompanyTransactions(
                companyId = companyId,
                period = analysisPeriod,
                types = transactionTypes
            )

            if (transactions.isEmpty()) {
                return Result.failure(IllegalStateException("There are no transactions to analyze in the specified period."))
            }

            val riskAnalysis = aiOrchestrator.analyzeComplianceRisk(
                companyId = companyId,
                transactions = transactions,
                period = analysisPeriod
            )

            aiDBI.saveComplianceAnalysis(riskAnalysis)

            logger.info {
                "Risk analysis completed: score=${riskAnalysis.riskScore}, " +
                        "audit probability=${riskAnalysis.auditProbability}"
            }

            Result.success(riskAnalysis)

        } catch (e: Exception) {
            logger.error(e) { "Error analyzing compliance risk" }
            Result.failure(e)
        }
    }

    suspend fun analyzeIndustryBenchmark(
        companyId: String,
        industry: String,
        companySize: String
    ): IndustryRiskAnalysis {

        val companyAnalysis = invoke(companyId).getOrNull()
        val industryBenchmark = taxComplianceAnalyzer.getIndustryBenchmarks(companyId)

        return IndustryRiskAnalysis(
            companyId = companyId,
            industry = industry,
            companySize = companySize,
            companyRiskScore = companyAnalysis?.riskScore ?: 0.0,
            industryAverageRisk = calculateIndustryAverage(industry),
            riskComparison = compareWithIndustry(
                companyAnalysis?.riskScore ?: 0.0,
                industry
            ),
            recommendations = generateIndustryRecommendations(
                companyAnalysis,
                industryBenchmark
            )
        )
    }

    suspend fun monitorHighRiskCompanies(
        riskThreshold: Double = 0.7,
        period: DateRange = DateRange.last30Days()
    ): List<HighRiskCompany> {

        val highRiskCompanies = aiDBI.getHighRiskCompanies(riskThreshold)

        return highRiskCompanies.map { companyId ->
            val analysis = invoke(companyId, period).getOrNull()
            val lastAnalysisInstant = analysis?.analysisDate ?: Clock.System.now()
            val recommendedActionStrings = analysis?.recommendations?.map { it.toString() } ?: emptyList()

            HighRiskCompany(
                companyId = companyId,
                riskScore = analysis?.riskScore ?: 0.0,
                auditProbability = analysis?.auditProbability ?: 0.0,
                highRiskPatterns = analysis?.riskPatterns?.size ?: 0,
                lastAnalysis = lastAnalysisInstant,
                recommendedActions = recommendedActionStrings
            )
        }
    }

    private fun calculateIndustryAverage(industry: String): Double {
        return when (industry) {
            "Retail" -> 0.3
            "Manufacturing" -> 0.4
            "Services" -> 0.25
            else -> 0.35
        }
    }

    private fun compareWithIndustry(companyRisk: Double, industry: String): RiskComparison {
        val industryAverage = calculateIndustryAverage(industry)
        val difference = companyRisk - industryAverage

        return when {
            difference > 0.2 -> RiskComparison.HIGHER
            difference < -0.1 -> RiskComparison.LOWER
            else -> RiskComparison.SIMILAR
        }
    }

    private fun generateIndustryRecommendations(
        companyAnalysis: ComplianceRiskAnalysis?,
        industryBenchmark: IndustryBenchmarks
    ): List<String> {
        val recommendations = mutableListOf<String>()

        companyAnalysis?.let { analysis ->
            if (analysis.riskScore > 0.7) {
                recommendations.add("Implement stricter internal controls")
                recommendations.add("Conduct a monthly internal audit")
            }

            if (analysis.auditProbability > 0.5) {
                recommendations.add("Prepare documentation for a possible audit")
                recommendations.add("Review compliance policies")
            }

            if (industryBenchmark.commonRiskPatterns.contains(RiskPatternType.ROUND_AMOUNTS)) {
                recommendations.add("Avoid transactions with exactly round amounts")
            }
        }

        return recommendations
    }
}