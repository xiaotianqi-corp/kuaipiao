package org.xiaotianqi.kuaipiao.data.sources.db.dbi.company.impl

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.core.logic.typedId.impl.DtId
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.company.CompanyDBI
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.company.CompaniesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.company.CompanyEntity
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.company.fromCompanyInfo
import org.xiaotianqi.kuaipiao.data.sources.db.toEntityId
import org.xiaotianqi.kuaipiao.domain.organization.CompanyInfo
import java.time.Instant
import java.util.UUID
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
class CompanyDBIImpl : CompanyDBI {

    override suspend fun create(data: CompanyInfo): CompanyEntity = dbQuery {
        CompanyEntity.new(UUID.randomUUID()) {
            fromCompanyInfo(data)
            createdAt = Instant.now()
        }
    }

    override suspend fun get(id: DtId<CompanyInfo>): CompanyEntity? = dbQuery {
        CompanyEntity.findById(id.id)
    }

    override suspend fun getById(companyId: String): CompanyEntity? = dbQuery {
        try {
            CompanyEntity.findById(UUID.fromString(companyId))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override suspend fun getByTaxId(taxId: String): CompanyEntity? = dbQuery {
        CompanyEntity.find { CompaniesTable.tax_id eq taxId }
            .limit(1)
            .firstOrNull()
    }

    override suspend fun getByIndustry(companyId: String): CompanyEntity? = dbQuery {
        try {
            CompanyEntity.findById(UUID.fromString(companyId))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override suspend fun updateIndustry(id: DtId<CompanyInfo>, industry: String) {
        dbQuery {
            CompaniesTable.update({ CompaniesTable.id eq id.toEntityId(CompaniesTable) }) {
                it[CompaniesTable.industry] = industry
                it[updated_at] = Instant.now()
            }
        }
    }

    override suspend fun delete(id: DtId<CompanyInfo>) {
        dbQuery {
            CompaniesTable.deleteWhere { CompaniesTable.id eq id.toEntityId(CompaniesTable) }
        }
    }
}