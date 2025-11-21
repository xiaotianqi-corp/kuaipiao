package org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks
import org.xiaotianqi.kuaipiao.enums.RiskPatternType

object BenchmarksTable : IdTable<String>("industry_benchmarks") {
    override val id: Column<EntityID<String>> = varchar("industry", 100).entityId()
    val avg_transaction_size = double("avg_transaction_size")
    val typical_transaction_count = integer("typical_transaction_count")
    val common_risk_patterns = text("common_risk_patterns")
    val tax_compliance_rate = double("tax_compliance_rate").default(0.95)
    val audit_probability = double("audit_probability").default(0.15)

    override val primaryKey = PrimaryKey(id)
}

class BenchmarkEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, BenchmarkEntity>(BenchmarksTable)

    var industry by BenchmarksTable.id
    var avgTransactionSize by BenchmarksTable.avg_transaction_size
    var typicalTransactionCount by BenchmarksTable.typical_transaction_count
    var commonRiskPatterns by BenchmarksTable.common_risk_patterns
    var taxComplianceRate by BenchmarksTable.tax_compliance_rate
    var auditProbability by BenchmarksTable.audit_probability
}

fun BenchmarkEntity.toIndustryBenchmarks() = IndustryBenchmarks(
    industry = industry.value,
    avgTransactionSize = avgTransactionSize,
    typicalTransactionCount = typicalTransactionCount,
    commonRiskPatterns = commonRiskPatterns.split(",")
        .mapNotNull { pattern ->
            runCatching { RiskPatternType.valueOf(pattern.trim()) }.getOrNull()
        },
    taxComplianceRate = taxComplianceRate,
    auditProbability = auditProbability
)

fun BenchmarkEntity.fromIndustryBenchmarks(data: IndustryBenchmarks) {
    avgTransactionSize = data.avgTransactionSize
    typicalTransactionCount = data.typicalTransactionCount
    commonRiskPatterns = data.commonRiskPatterns.joinToString(",") { it.name }
    taxComplianceRate = data.taxComplianceRate
    auditProbability = data.auditProbability
}