package org.xiaotianqi.kuaipiao.data.daos.ai

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.ComplianceRiskEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.ComplianceRiskTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.RiskPatternEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai.RiskPatternsTable
import org.xiaotianqi.kuaipiao.enums.SeverityStatus
import org.xiaotianqi.kuaipiao.domain.compliance.*
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.risk.RiskPattern
import org.xiaotianqi.kuaipiao.enums.RiskPatternType
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Single
@ExperimentalTime
class ComplianceRiskDao {

    suspend fun saveComplianceAnalysis(analysis: ComplianceRiskAnalysis): String = transaction {
        val entity = ComplianceRiskEntity.new {
            companyId = analysis.companyId
            periodStart = analysis.period.start.toJavaInstant()
            periodEnd = analysis.period.end.toJavaInstant()
            riskScore = analysis.riskScore
            auditProbability = analysis.auditProbability
            highRiskTransactionCount = analysis.highRiskTransactions.size
            recommendations = analysis.recommendations.joinToString(";;")
                            { "${it.code}|${it.description}|${it.priority}|${it.estimatedEffort}" }
            nextReviewDate = analysis.nextReviewDate.toJavaInstant()
            analysisDate = analysis.analysisDate.toJavaInstant()
            createdAt = Instant.now()
        }

        analysis.riskPatterns.forEach { pattern ->
            RiskPatternEntity.new {
                this.analysis = entity
                patternType = pattern.patternType.name
                description = pattern.description
                severity = pattern.severity.name
                occurrences = pattern.occurrences
                totalAmount = pattern.totalAmount.toBigDecimal()
                firstOccurrence = pattern.firstOccurrence.toJavaInstant()
                lastOccurrence = pattern.lastOccurrence.toJavaInstant()
            }
        }

        entity.id.value.toString()
    }

    suspend fun getCompanyRiskHistory(companyId: String, limit: Int = 10): List<ComplianceRiskAnalysis> = transaction {
        ComplianceRiskTable
            .selectAll()
            .where { ComplianceRiskTable.companyId eq companyId }
            .orderBy(ComplianceRiskTable.analysisDate to SortOrder.DESC)
            .limit(limit)
            .map { row ->
                ComplianceRiskAnalysis(
                    companyId = row[ComplianceRiskTable.companyId],
                    period = DateRange(
                        start = row[ComplianceRiskTable.periodStart].toKotlinInstant(),
                        end = row[ComplianceRiskTable.periodEnd].toKotlinInstant()
                    ),
                    riskScore = row[ComplianceRiskTable.riskScore],
                    riskPatterns = getRiskPatternsForAnalysis(row[ComplianceRiskTable.id].value),
                    highRiskTransactions = emptyList(),
                    auditProbability = row[ComplianceRiskTable.auditProbability],
                    recommendations = row[ComplianceRiskTable.recommendations].split(";;").mapNotNull { rec ->
                        val parts = rec.split("|")
                        if (parts.size >= 4) {
                            ComplianceRecommendation(
                                code = parts[0],
                                description = parts[1],
                                priority = enumValueOf<SeverityStatus>(parts[2]),
                                estimatedEffort = parts[3]
                            )
                        } else null
                    },
                    nextReviewDate = row[ComplianceRiskTable.nextReviewDate].toKotlinInstant(),
                    analysisDate = row[ComplianceRiskTable.analysisDate].toKotlinInstant()
                )
            }
    }

    suspend fun getHighRiskCompanies(riskThreshold: Double = 0.7): List<String> = transaction {
        ComplianceRiskTable
            .select(ComplianceRiskTable.companyId)
            .where {
                (ComplianceRiskTable.riskScore greater riskThreshold) and
                        (ComplianceRiskTable.analysisDate greater Instant.now().minusSeconds(30 * 86400))
            }
            .map { it[ComplianceRiskTable.companyId] }
            .distinct()
    }

    private fun getRiskPatternsForAnalysis(analysisId: UUID): List<RiskPattern> {
        return RiskPatternsTable
            .selectAll()
            .where { RiskPatternsTable.analysisId eq analysisId }
            .map { row ->
                RiskPattern(
                    patternType = enumValueOf<RiskPatternType>(row[RiskPatternsTable.patternType]),
                    description = row[RiskPatternsTable.description],
                    severity = enumValueOf<SeverityStatus>(row[RiskPatternsTable.severity]),
                    occurrences = row[RiskPatternsTable.occurrences],
                    totalAmount = row[RiskPatternsTable.totalAmount].toDouble(),
                    firstOccurrence = row[RiskPatternsTable.firstOccurrence].toKotlinInstant(),
                    lastOccurrence = row[RiskPatternsTable.lastOccurrence].toKotlinInstant()
                )
            }
    }
}