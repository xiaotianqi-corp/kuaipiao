package org.xiaotianqi.kuaipiao.data.sources.db.dbi.benchmark

import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark.BenchmarkEntity
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks

interface BenchmarkDBI : DBI {
    suspend fun create(data: IndustryBenchmarks): BenchmarkEntity
    suspend fun getByIndustry(industry: String): BenchmarkEntity?
    suspend fun getAll(): List<BenchmarkEntity>
    suspend fun update(industry: String, data: IndustryBenchmarks)
    suspend fun delete(industry: String)
}