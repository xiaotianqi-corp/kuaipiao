package org.xiaotianqi.kuaipiao.data.daos.company

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.company.CompanyDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.company.toCompanyInfo
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class CompanyDao(
    private val companyDBI: CompanyDBI,
) {

    suspend fun create(data: CompanyInfo): CompanyInfo {
        val entity = companyDBI.create(data)
        return entity.toCompanyInfo()
    }

    suspend fun get(id: DtId<CompanyInfo>): CompanyInfo? {
        return companyDBI.get(id)?.toCompanyInfo()
    }

    suspend fun getByTaxId(taxId: String): CompanyInfo? {
        return companyDBI.getByTaxId(taxId)?.toCompanyInfo()
    }

    suspend fun getById(companyId: String): CompanyInfo? {
        return companyDBI.getById(companyId)?.toCompanyInfo()
    }

    suspend fun getByIndustry(companyId: String): CompanyInfo? {
        return companyDBI.getByIndustry(companyId)?.toCompanyInfo()
    }

    suspend fun updateIndustry(id: DtId<CompanyInfo>, industry: String) {
        companyDBI.updateIndustry(id, industry)
    }

    suspend fun delete(id: DtId<CompanyInfo>) {
        companyDBI.delete(id)
    }
}