package org.xiaotianqi.kuaipiao.data.sources.db.dbi.benchmark.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.benchmark.BenchmarkDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark.BenchmarkEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark.BenchmarksTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark.fromIndustryBenchmarks
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks

@Single(createdAtStart = true)
class BenchmarkDBIImpl : BenchmarkDBI {

    override suspend fun create(data: IndustryBenchmarks): BenchmarkEntity = dbQuery {
        BenchmarkEntity.new(data.industry) {
            fromIndustryBenchmarks(data)
        }
    }

    override suspend fun getByIndustry(industry: String): BenchmarkEntity? = dbQuery {
        BenchmarkEntity.findById(industry)
    }

    override suspend fun getAll(): List<BenchmarkEntity> = dbQuery {
        BenchmarkEntity.all().toList()
    }

    override suspend fun update(industry: String, data: IndustryBenchmarks) {
        dbQuery {
            BenchmarksTable.update({ BenchmarksTable.id eq industry }) {
                it[avg_transaction_size] = data.avgTransactionSize
                it[typical_transaction_count] = data.typicalTransactionCount
                it[common_risk_patterns] = data.commonRiskPatterns.joinToString(",") { p -> p.name }
                it[tax_compliance_rate] = data.taxComplianceRate
                it[audit_probability] = data.auditProbability
            }
        }
    }

    override suspend fun delete(industry: String) {
        dbQuery {
            BenchmarksTable.deleteWhere { BenchmarksTable.id eq industry }
        }
    }
}