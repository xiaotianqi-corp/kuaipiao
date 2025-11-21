package org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.impl

import org.koin.core.annotation.Single
import org.xiaotianqi.kuaipiao.data.daos.ai.*
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingReconciliationResult
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceRiskAnalysis
import org.xiaotianqi.kuaipiao.domain.document.DateRange
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.models.ModelResult
import org.xiaotianqi.kuaipiao.domain.transaction.TransactionData
import org.xiaotianqi.kuaipiao.domain.product.ProductClassificationData
import org.xiaotianqi.kuaipiao.domain.sales.SalesPrediction
import org.xiaotianqi.kuaipiao.domain.sales.SalesData
import java.time.Instant
import kotlin.time.ExperimentalTime

@Single(createdAtStart = true)
@ExperimentalTime
@ExperimentalStdlibApi
class AiDBIImpl(
    private val documentProcessingDao: DocumentProcessingDao,
    private val complianceRiskDao: ComplianceRiskDao,
    private val aiCacheDao: AiCacheDao,
    private val modelResultDao: ModelResultDao
) : AiDBI {

    override suspend fun saveInvoiceProcessing(userId: String, companyId: String, result: InvoiceProcessingResult) =
        dbQuery { documentProcessingDao.saveInvoiceProcessing(userId, companyId, result) }

    override suspend fun getProcessingHistory(userId: String, limit: Int, offset: Int) =
        dbQuery { documentProcessingDao.getProcessingHistory(userId, limit, offset) }

    override suspend fun getCompanyProcessingStats(companyId: String) =
        dbQuery { documentProcessingDao.getCompanyProcessingStats(companyId) }

    override suspend fun saveComplianceAnalysis(analysis: ComplianceRiskAnalysis) =
        dbQuery { complianceRiskDao.saveComplianceAnalysis(analysis) }

    override suspend fun getCompanyRiskHistory(companyId: String, limit: Int) =
        dbQuery { complianceRiskDao.getCompanyRiskHistory(companyId, limit) }

    override suspend fun getHighRiskCompanies(riskThreshold: Double) =
        dbQuery { complianceRiskDao.getHighRiskCompanies(riskThreshold) }

    override suspend fun getProviderStats(startDate: Instant, endDate: Instant) =
        dbQuery { modelResultDao.getProviderStats(startDate, endDate) }

    override suspend fun getOperationStats(provider: String, days: Int) =
        dbQuery { modelResultDao.getOperationStats(provider, days) }

    override suspend fun getCostAnalysis(startDate: Instant, endDate: Instant) =
        dbQuery { modelResultDao.getCostAnalysis(startDate, endDate) }

    override suspend fun saveModelResult(data: ModelResult) =
        dbQuery {
            modelResultDao.saveModelResult(
                modelType = data.modelType,
                aiProvider = data.aiProvider,
                operation = data.operation,
                inputHash = data.inputHash,
                inputData = data.inputData,
                outputData = data.outputData,
                confidence = data.confidence,
                processingTime = data.processingTime
            )
        }

    override suspend fun getCompanyTransactions(
        companyId: String,
        period: DateRange,
        types: List<String>
    ): List<TransactionData> = dbQuery {
        // TODO: Implementar cuando exista TransactionDao
        emptyList()
    }

    override suspend fun saveProductClassification(
        userId: String,
        companyId: String,
        classification: ProductClassificationData
    ): String = dbQuery {
        modelResultDao.saveModelResult(
            modelType = "PRODUCT_CLASSIFICATION",
            aiProvider = "openai",
            operation = "classify_product",
            inputHash = classification.productName.hashCode().toString(),
            inputData = classification.productName,
            outputData = "${classification.tariffCode}|${classification.suggestedCategory}",
            confidence = classification.confidence,
            processingTime = 0L
        )
    }

    override suspend fun saveDocumentExtraction(
        userId: String,
        companyId: String,
        result: DocumentExtractionResult
    ): String = dbQuery {
        modelResultDao.saveModelResult(
            modelType = "DOCUMENT_EXTRACTION",
            aiProvider = "openai",
            operation = "extract_${result.documentType.name.lowercase()}",
            inputHash = "${userId}_${companyId}_${result.documentIndex}".hashCode().toString(),
            inputData = result.rawText,
            outputData = result.extractedData.toString(),
            confidence = result.confidence,
            processingTime = result.processingTimeMs,
            success = result.success
        )
    }

    override suspend fun getSalesHistory(companyId: String, days: Int): List<SalesData> = dbQuery {
        // TODO: Implementar cuando exista SalesDao
        emptyList()
    }

    override suspend fun saveSalesPrediction(companyId: String, prediction: SalesPrediction): String = dbQuery {
        modelResultDao.saveModelResult(
            modelType = "SALES_FORECAST",
            aiProvider = "openai",
            operation = "predict_sales",
            inputHash = "${companyId}_${prediction.periodDays}".hashCode().toString(),
            inputData = null,
            outputData = prediction.predictedGrowth.toString(),
            confidence = prediction.confidence,
            processingTime = 0L
        )
    }

    override suspend fun getAccountingPatterns(companyId: String): List<AccountingPattern> = dbQuery {
        // TODO: Implementar cuando exista AccountingPatternDao
        emptyList()
    }

    override suspend fun saveAccountingReconciliation(
        userId: String,
        companyId: String,
        result: AccountingReconciliationResult
    ): String = dbQuery {
        modelResultDao.saveModelResult(
            modelType = "ACCOUNTING_RECONCILIATION",
            aiProvider = "openai",
            operation = "reconcile_accounting",
            inputHash = "${userId}_${companyId}_${result.reconciliation.documentDate}".hashCode().toString(),
            inputData = result.reconciliation.documentDate,
            outputData = result.suggestedEntries.toString(),
            confidence = result.confidenceScore,
            processingTime = 0L
        )
    }

    override suspend fun getReconciliationHistory(
        companyId: String,
        limit: Int
    ): List<AccountingReconciliationResult> = dbQuery {
        // TODO: Implementar cuando exista ReconciliationDao
        emptyList()
    }
}