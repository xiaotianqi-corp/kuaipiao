package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.xiaotianqi.kuaipiao.core.logic.ai.AiOrchestrator
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.domain.compliance.ComplianceCheck
import org.xiaotianqi.kuaipiao.domain.document.BulkExtractionResult
import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.domain.document.DocumentRequest
import org.xiaotianqi.kuaipiao.domain.document.ExtractionError
import org.xiaotianqi.kuaipiao.domain.document.ExtractionSummary
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.domain.trade.ExportDocumentResult
import org.xiaotianqi.kuaipiao.enums.FileType
import org.xiaotianqi.kuaipiao.enums.DocumentType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

private val logger = KotlinLogging.logger {}

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
class ExtractDocumentDataUseCase(
    private val aiOrchestrator: AiOrchestrator,
    private val aiDBI: AiDBI
) {

    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileType: FileType,
        documentType: DocumentType,
        userId: String,
        companyId: String,
        country: String
    ): Result<DocumentExtractionResult> {

        logger.info { "Extracting data from document: $documentType" }

        return try {

            validateDocument(fileBytes, fileType, documentType)

            val result = when (documentType) {
                DocumentType.INVOICE -> extractInvoiceData(fileBytes, fileType, country)
                DocumentType.RECEIPT -> extractReceiptData(fileBytes, fileType, country)
                DocumentType.CONTRACT -> extractContractData(fileBytes, fileType, country)
                DocumentType.IDENTIFICATION -> extractIdentificationData(fileBytes, fileType, country)
                else -> throw IllegalArgumentException("Unsupported document type: $documentType")
            }

            aiDBI.saveDocumentExtraction(
                userId = userId,
                companyId = companyId,
                result = result
            )

            logger.info {
                "Data extracted: ${result.extractedData.size} fields " +
                        "(trust: ${result.confidence})"
            }

            Result.success(result)

        } catch (e: Exception) {
            logger.error(e) { "Error extracting data from document" }
            Result.failure(e)
        }
    }

    suspend fun extractMultipleDocuments(
        documents: List<DocumentRequest>,
        userId: String,
        companyId: String
    ): BulkExtractionResult {

        val results = mutableListOf<DocumentExtractionResult>()
        val errors = mutableListOf<ExtractionError>()

        documents.forEachIndexed { index, request ->
            try {
                val result = invoke(
                    fileBytes = request.fileBytes,
                    fileType = request.fileType,
                    documentType = request.documentType,
                    userId = userId,
                    companyId = companyId,
                    country = request.country
                ).getOrThrow()

                results.add(result)
            } catch (e: Exception) {
                errors.add(ExtractionError(
                    documentIndex = index,
                    documentType = request.documentType,
                    error = e.message ?: "Unknown error"
                ))
            }
        }

        return BulkExtractionResult(
            successful = results,
            failed = errors,
            summary = ExtractionSummary(
                totalDocuments = documents.size,
                extracted = results.size,
                failed = errors.size,
                totalFields = results.sumOf { it.extractedData.size }
            )
        )
    }

    private suspend fun extractInvoiceData(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): DocumentExtractionResult {

        val exportResult = aiOrchestrator.processExportDocument(
            fileBytes = fileBytes,
            fileType = fileType,
            exporterCountry = country,
            importerCountry = "EC",
            productDescriptions = emptyList()
        )

        val extracted = extractFieldsFromExportInvoice(exportResult)

        val success = exportResult.complianceCheck.isCompliant

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.INVOICE,
            success = success,
            confidence = exportResult.documentDataConfidenceOrFallback(),
            extractedData = extracted,
            processingTimeMs = exportResult.processingTime,
            rawText = exportResult.rawText
        )
    }

    private fun extractFieldsFromExport(exportResult: ExportDocumentResult): Map<String, JsonElement> {
        val doc = exportResult.documentData
        val map = mutableMapOf<String, JsonElement>()
        val json = Json { ignoreUnknownKeys = true }

        map["document_id"] = JsonPrimitive(doc.documentId)
        map["issue_date"] = JsonPrimitive(doc.issueDate.toString())
        map["currency"] = JsonPrimitive(doc.currency)
        map["total"] = JsonPrimitive(doc.extractedData.total)

        doc.extractedData.items.let { map["items"] = json.encodeToJsonElement(it) }
        doc.extractedData.taxes.let { map["taxes"] = json.encodeToJsonElement(it) }

        map["ai_provider"] = JsonPrimitive(exportResult.aiProvider )
        map["processing_time_ms"] = JsonPrimitive(exportResult.processingTime)
        map["confidence"] = JsonPrimitive(exportResult.documentData.confidence)

        return map
    }

    private suspend fun extractReceiptData(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): DocumentExtractionResult {

        val result = aiOrchestrator.processExportDocument(
            fileBytes = fileBytes,
            fileType = fileType,
            exporterCountry = country,
            importerCountry = "EC",
            productDescriptions = emptyList()
        )

        val extractedData = extractFieldsFromExport(result)

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.RECEIPT,
            success = extractedData.isNotEmpty(),
            confidence = 0.85,
            extractedData = extractedData,
            processingTimeMs = result.processingTime,
            rawText = result.rawText
        )
    }


    private suspend fun extractContractData(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): DocumentExtractionResult {

        val result = aiOrchestrator.processExportDocument(
            fileBytes = fileBytes,
            fileType = fileType,
            exporterCountry = country,
            importerCountry = "EC",
            productDescriptions = emptyList()
        )

        val extractedData = extractFieldsFromExport(result)

        return DocumentExtractionResult(
            documentIndex = 0,
            documentType = DocumentType.CONTRACT,
            success = true,
            confidence = result.documentDataConfidenceOrFallback(),
            extractedData = extractedData,
            processingTimeMs = result.processingTime,
            rawText = result.rawText
        )
    }


    private suspend fun extractIdentificationData(
        fileBytes: ByteArray,
        fileType: FileType,
        country: String
    ): DocumentExtractionResult {

        val analysisStart = Clock.System.now().toEpochMilliseconds()

        val prompt = """
        Extract the data from the country's identification document: $country.
        
        It ALWAYS returns a valid JSON with this exact structure:
        {
          "names": "string",
          "surnames": "string",
          "number": "string",
          "issueDate": "YYYY-MM-DD",
          "expirationDate": "YYYY-MM-DD",
          "confidence": 0.0
        }
        
        If any data does not exist, return empty.
    """.trimIndent()

        return try {

            val aiResult = aiOrchestrator.extractDocument(
                prompt = prompt,
                fileBytes = fileBytes,
                fileType = fileType,
                operation = "extract_identification_$country"
            )

            val elapsed = Clock.System.now().toEpochMilliseconds() - analysisStart

            val jsonString = aiResult.rawText ?: "{}"

            val parsed = runCatching {
                Json.decodeFromString<Map<String, Any?>>(jsonString)
            }.getOrElse {
                logger.error { "AI identification parsing error: ${it.message}" }
                emptyMap()
            }

            val json = Json { ignoreUnknownKeys = true }
            val extracted: Map<String, JsonElement> = parsed.mapValues { (_, value) ->
                json.encodeToJsonElement(value)
            }

            DocumentExtractionResult(
                documentIndex = 0,
                documentType = DocumentType.IDENTIFICATION,
                success = true,
                confidence = (parsed["confidence"] as? Double) ?: 0.85,
                extractedData = extracted,
                rawText = jsonString,
                processingTimeMs = elapsed
            )

        } catch (e: Exception) {

            val elapsed = Clock.System.now().toEpochMilliseconds() - analysisStart
            logger.error(e) { "Identification extraction failed from AI provider" }

            DocumentExtractionResult(
                documentIndex = 0,
                documentType = DocumentType.IDENTIFICATION,
                success = false,
                confidence = 0.0,
                extractedData = emptyMap(),
                rawText = e.message,
                processingTimeMs = elapsed
            )
        }
    }

    private fun buildInvoiceFieldMap(
        invoiceResult: InvoiceProcessingResult,
        processingTime: Long,
        compliance: ComplianceCheck? = null
    ): Map<String, JsonElement> {
        val json = Json { ignoreUnknownKeys = true }
        val map = mutableMapOf<String, JsonElement>()
        val data = invoiceResult.extractedData
        val customer = data.customerId
        map["document_id"] = JsonPrimitive(invoiceResult.documentId)
        map["invoice_id"] = JsonPrimitive(invoiceResult.invoiceId)
        map["supplier_name"] = JsonPrimitive(invoiceResult.supplierName)
        map["total_amount"] = JsonPrimitive(invoiceResult.totalAmount)
        map["currency"] = JsonPrimitive(invoiceResult.currency)
        map["issue_date"] = JsonPrimitive(invoiceResult.issueDate.toString())
        map["tax_amount"] = JsonPrimitive(invoiceResult.taxAmount)
        map["confidence"] = JsonPrimitive(invoiceResult.confidence)
        map["status"] = JsonPrimitive(invoiceResult.status.name)
        map["processing_time_ms"] = JsonPrimitive(processingTime)
        map["ai_provider"] = JsonPrimitive(invoiceResult.aiProvider)
        map["document_type"] = JsonPrimitive(customer.documentType.name)
        map["invoice_number"] = JsonPrimitive(data.number)
        map["invoice_date"] = JsonPrimitive(data.date.toString())
        map["invoice_due_date"] = JsonPrimitive(data.dueDate.toString())
        map["subtotal"] = JsonPrimitive(data.subtotal)
        map["tax"] = JsonPrimitive(data.tax)
        map["total"] = JsonPrimitive(data.total)
        map["invoice_currency"] = JsonPrimitive(data.currency)
        map["customer_first_name"] = JsonPrimitive(customer.firstName ?: "")
        map["customer_last_name"] = JsonPrimitive(customer.lastName ?: "")
        map["customer_business_name"] = JsonPrimitive(customer.businessName ?: "")
        map["customer_type"] = JsonPrimitive(customer.customerType.name)
        map["customer_document_type"] = JsonPrimitive(customer.documentType.name)
        map["customer_document_number"] = JsonPrimitive(customer.documentNumber ?: "")
        map["customer_email"] = JsonPrimitive(customer.email ?: "")
        map["customer_phone"] = JsonPrimitive(customer.phone ?: "")
        map["customer_address"] = json.encodeToJsonElement(customer.address)
        map["customer_date_of_birth"] = JsonPrimitive(customer.dateOfBirth ?: "")
        map["customer_issue_date"] = JsonPrimitive(customer.issueDate ?: "")
        map["customer_expiration_date"] = JsonPrimitive(customer.expirationDate ?: "")
        map["customer_raw_text"] = JsonPrimitive(customer.rawText ?: "")
        map["items"] = json.encodeToJsonElement(data.items)
        map["validation_errors"] = json.encodeToJsonElement(invoiceResult.validationErrors)
        map["suggestions"] = json.encodeToJsonElement(invoiceResult.suggestions)
        compliance?.let { c ->
            map["is_compliant"] = JsonPrimitive(c.isCompliant)
            map["compliance_score"] = JsonPrimitive(c.riskScore)
            map["compliance_issues"] = json.encodeToJsonElement(c.issues ?: emptyList<String>())
        }

        return map
    }

    private fun extractFieldsFromExportInvoice(export: ExportDocumentResult): Map<String, JsonElement> {
        return buildInvoiceFieldMap(
            invoiceResult = export.documentData,
            processingTime = export.processingTime,
            compliance = export.complianceCheck
        )
    }

    private fun extractFieldsFromInvoice(invoiceResult: InvoiceProcessingResult): Map<String, JsonElement> {
        return buildInvoiceFieldMap(
            invoiceResult = invoiceResult,
            processingTime = invoiceResult.processingTime,
            compliance = null
        )
    }

    private fun ExportDocumentResult.documentDataConfidenceOrFallback(): Double {
        return try {
            val field = this::class.members.find { it.name == "confidence" }
            if (field != null) {
                (this::class.java.getDeclaredField("confidence").getDouble(this))
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun ExportDocumentResult.baseCurrencyOrFallback(): String = this.documentData.currency

    private fun ExportDocumentResult.confidenceEstimateOrZero(): Double = try {
        val field = this::class.members.find { it.name == "confidence" }
        if (field != null) 0.0 else 0.0
    } catch (_: Exception) { 0.0 }


    private fun validateDocument(
        fileBytes: ByteArray,
        fileType: FileType,
        documentType: DocumentType
    ) {
        if (fileBytes.isEmpty()) {
            throw IllegalArgumentException("Empty document")
        }

        when (documentType) {
            DocumentType.IDENTIFICATION -> {
                if (fileBytes.size > 2 * 1024 * 1024) {
                    throw IllegalArgumentException("Identification document too large")
                }
            }
            DocumentType.CONTRACT -> {
                if (fileBytes.size > 5 * 1024 * 1024) {
                    throw IllegalArgumentException("Contract too big")
                }
            }
            else -> {
                if (fileBytes.size > 10 * 1024 * 1024) {
                    throw IllegalArgumentException("Document too large")
                }
            }
        }
    }
}
