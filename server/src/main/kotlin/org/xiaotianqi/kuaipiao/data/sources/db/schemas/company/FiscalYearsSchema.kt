package org.xiaotianqi.kuaipiao.data.sources.db.schemas.company

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.xiaotianqi.kuaipiao.domain.organization.FiscalYearInfo
import org.xiaotianqi.kuaipiao.enums.AuditStatus
import java.util.*
import kotlin.time.ExperimentalTime

object FiscalYearsTable : UUIDTable("fiscal_years") {
    val company = reference(
        name = "company_id",
        foreign = CompaniesTable,
        onDelete = ReferenceOption.CASCADE
    ).index()
    val year = integer("year")
    val total_revenue = double("total_revenue")
    val total_expenses = double("total_expenses")
    val net_income = double("net_income")
    val audit_status = enumerationByName<AuditStatus>("audit_status", 20)
}

class FiscalYearEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<FiscalYearEntity>(FiscalYearsTable)

    var company by CompanyEntity referencedOn FiscalYearsTable.company
    var year by FiscalYearsTable.year
    var totalRevenue by FiscalYearsTable.total_revenue
    var totalExpenses by FiscalYearsTable.total_expenses
    var netIncome by FiscalYearsTable.net_income
    var auditStatus by FiscalYearsTable.audit_status
}

@ExperimentalTime
fun FiscalYearEntity.toFiscalYearInfo() = FiscalYearInfo(
    year = year,
    totalRevenue = totalRevenue,
    totalExpenses = totalExpenses,
    netIncome = netIncome,
    auditStatus = auditStatus
)