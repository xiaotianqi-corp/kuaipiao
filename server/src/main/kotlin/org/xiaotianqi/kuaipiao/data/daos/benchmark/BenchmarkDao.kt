package org.xiaotianqi.kuaipiao.data.daos.benchmark

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.benchmark.BenchmarkDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.benchmark.toIndustryBenchmarks
import org.xiaotianqi.kuaipiao.domain.benchmark.IndustryBenchmarks

@Single(createdAtStart = true)
class BenchmarkDao(
    private val benchmarkDBI: BenchmarkDBI
) {

    suspend fun create(data: IndustryBenchmarks): IndustryBenchmarks {
        val entity = benchmarkDBI.create(data)
        return entity.toIndustryBenchmarks()
    }

    suspend fun findByIndustry(industry: String): IndustryBenchmarks? {
        return benchmarkDBI.getByIndustry(industry)?.toIndustryBenchmarks()
    }

    suspend fun getAll(): List<IndustryBenchmarks> {
        return benchmarkDBI.getAll().map { it.toIndustryBenchmarks() }
    }

    suspend fun update(industry: String, data: IndustryBenchmarks) {
        benchmarkDBI.update(industry, data)
    }

    suspend fun delete(industry: String) {
        benchmarkDBI.delete(industry)
    }
}