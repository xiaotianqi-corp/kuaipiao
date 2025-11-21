package org.xiaotianqi.kuaipiao.data.sources.db.dbi.company

import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.company.CompanyEntity
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface CompanyDBI : DBI {
    suspend fun create(data: CompanyInfo): CompanyEntity
    suspend fun get(id: DtId<CompanyInfo>): CompanyEntity?
    suspend fun getById(companyId: String): CompanyEntity?
    suspend fun getByTaxId(taxId: String): CompanyEntity?
    suspend fun getByIndustry(companyId: String): CompanyEntity?
    suspend fun updateIndustry(id: DtId<CompanyInfo>, industry: String)
    suspend fun delete(id: DtId<CompanyInfo>)
}