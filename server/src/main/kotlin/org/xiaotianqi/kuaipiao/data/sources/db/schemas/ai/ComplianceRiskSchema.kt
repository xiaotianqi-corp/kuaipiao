package org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object ComplianceRiskTable : UUIDTable("compliance_risk_analysis") {
    val companyId = varchar("company_id", 36).index()
    val periodStart = timestamp("period_start")
    val periodEnd = timestamp("period_end")
    val riskScore = double("risk_score")
    val auditProbability = double("audit_probability")
    val highRiskTransactionCount = integer("high_risk_transaction_count")
    val recommendations = text("recommendations")
    val nextReviewDate = timestamp("next_review_date")
    val analysisDate = timestamp("analysis_date")
    val createdAt = timestamp("created_at")
}

object RiskPatternsTable : UUIDTable("compliance_risk_patterns") {
    val analysisId = reference("analysis_id", ComplianceRiskTable, onDelete = ReferenceOption.CASCADE)
    val patternType = varchar("pattern_type", 50)
    val description = text("description")
    val severity = varchar("severity", 20)
    val occurrences = integer("occurrences")
    val totalAmount = decimal("total_amount", 10, 2)
    val firstOccurrence = timestamp("first_occurrence")
    val lastOccurrence = timestamp("last_occurrence")
}

class ComplianceRiskEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ComplianceRiskEntity>(ComplianceRiskTable)
    var companyId by ComplianceRiskTable.companyId
    var periodStart by ComplianceRiskTable.periodStart
    var periodEnd by ComplianceRiskTable.periodEnd
    var riskScore by ComplianceRiskTable.riskScore
    var auditProbability by ComplianceRiskTable.auditProbability
    var highRiskTransactionCount by ComplianceRiskTable.highRiskTransactionCount
    var recommendations by ComplianceRiskTable.recommendations
    var nextReviewDate by ComplianceRiskTable.nextReviewDate
    var analysisDate by ComplianceRiskTable.analysisDate
    var createdAt by ComplianceRiskTable.createdAt
}

class RiskPatternEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RiskPatternEntity>(RiskPatternsTable)
    var analysis by ComplianceRiskEntity referencedOn RiskPatternsTable.analysisId
    var patternType by RiskPatternsTable.patternType
    var description by RiskPatternsTable.description
    var severity by RiskPatternsTable.severity
    var occurrences by RiskPatternsTable.occurrences
    var totalAmount by RiskPatternsTable.totalAmount
    var firstOccurrence by RiskPatternsTable.firstOccurrence
    var lastOccurrence by RiskPatternsTable.lastOccurrence
}