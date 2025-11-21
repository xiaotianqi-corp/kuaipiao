package org.xiaotianqi.kuaipiao.domain.document

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceItemData
import org.xiaotianqi.kuaipiao.domain.tax.TaxDetail
import org.xiaotianqi.kuaipiao.enums.DocumentType
import org.xiaotianqi.kuaipiao.enums.FileType

@Serializable
data class DocumentData(
    val documentId: String? = null,
    val invoiceNumber: String? = null,
    val issueDate: String? = null,
    val currency: String? = null,
    val emitterRuc: String? = null,
    val emitterName: String? = null,
    val receiverRuc: String? = null,
    val receiverName: String? = null,
    val subtotal: Double? = null,
    val taxAmount: Double? = null,
    val total: Double? = null,
    val taxes: List<TaxDetail> = emptyList(),
    val items: List<InvoiceItemData> = emptyList(),
    val country: String? = null,
    val rawContent: String? = null,
) {
    fun toJson(): String = Json.encodeToString(this)
}

@Serializable
data class DocumentExtractionResult(
    val documentIndex: Int,
    val documentType: DocumentType,
    val success: Boolean,
    val confidence: Double,
    val rawText: String?,
    val extractedData: Map<String, JsonElement>,
    val processingTimeMs: Long
)

@Serializable
data class DocumentRequest(
    val fileBytes: ByteArray,
    val fileType: FileType,
    val documentType: DocumentType,
    val country: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DocumentRequest) return false
        if (!fileBytes.contentEquals(other.fileBytes)) return false
        if (fileType != other.fileType) return false
        if (documentType != other.documentType) return false
        if (country != other.country) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileBytes.contentHashCode()
        result = 31 * result + fileType.hashCode()
        result = 31 * result + documentType.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }
}

@Serializable
data class BulkExtractionResult(
    val successful: List<DocumentExtractionResult>,
    val failed: List<ExtractionError>,
    val summary: ExtractionSummary
)

@Serializable
data class ExtractionError(
    val documentIndex: Int,
    val documentType: DocumentType,
    val error: String
)

@Serializable
data class ExtractionSummary(
    val totalDocuments: Int,
    val extracted: Int,
    val failed: Int,
    val totalFields: Int
)
