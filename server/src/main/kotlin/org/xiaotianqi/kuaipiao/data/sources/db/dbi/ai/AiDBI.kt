package org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.DBI
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.models.CostAnalysis
import org.xiaotianqi.kuaipiao.domain.models.ModelResult
import org.xiaotianqi.kuaipiao.domain.models.OperationStats
import org.xiaotianqi.kuaipiao.domain.models.ProviderStats
import org.xiaotianqi.kuaipiao.domain.processing.*
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData
import org.xiaotianqi.kuaipiao.domain.sales.SalesData
import org.xiaotianqi.kuaipiao.domain.sales.SalesPrediction
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import java.time.Instant
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
interface AiDBI : DBI {
    suspend fun saveInvoiceProcessing(userId: String, companyId: String, result: InvoiceProcessingResult): String
    suspend fun getProcessingHistory(userId: String, limit: Int, offset: Int): List<ProcessingHistoryItem>
    suspend fun getCompanyProcessingStats(companyId: String): ProcessingStats
    suspend fun saveComplianceAnalysis(analysis: ComplianceRiskAnalysis): String
    suspend fun saveModelResult(data: ModelResult): String
    suspend fun getCompanyRiskHistory(companyId: String, limit: Int): List<ComplianceRiskAnalysis>
    suspend fun getHighRiskCompanies(riskThreshold: Double): List<String>
    suspend fun getProviderStats(startDate: Instant, endDate: Instant): List<ProviderStats>
    suspend fun getOperationStats(provider: String, days: Int): List<OperationStats>
    suspend fun getCostAnalysis(startDate: Instant, endDate: Instant): CostAnalysis
    suspend fun getCompanyTransactions(companyId: String, period: DateRange, types: List<String>): List<TransactionData>
    suspend fun saveProductClassification(userId: String, companyId: String, classification: ProductClassificationData): String
    suspend fun saveDocumentExtraction(userId: String, companyId: String, result: DocumentExtractionResult): String
    suspend fun getSalesHistory(companyId: String, days: Int): List<SalesData>
    suspend fun saveSalesPrediction(companyId: String, prediction: SalesPrediction): String
    suspend fun getAccountingPatterns(companyId: String): List<AccountingPattern>
    suspend fun saveAccountingReconciliation(userId: String, companyId: String, result: AccountingReconciliationResult): String
    suspend fun getReconciliationHistory(companyId: String, limit: Int): List<AccountingReconciliationResult>
}