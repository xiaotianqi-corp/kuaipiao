package org.xiaotianqi.kuaipiao.domain.invoice

import org.xiaotianqi.kuaipiao.domain.accounting.AccountSuggestion
import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.accounting.AccountingPattern
import org.xiaotianqi.kuaipiao.enums.DocumentType

@Serializable
data class InvoiceExtractionRequest(
    val imageData: ByteArray,
    val documentType: DocumentType,
    val historicalPatterns: List<AccountingPattern> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as InvoiceExtractionRequest
        if (!imageData.contentEquals(other.imageData)) return false
        if (documentType != other.documentType) return false
        if (historicalPatterns != other.historicalPatterns) return false
        return true
    }

    override fun hashCode(): Int {
        var result = imageData.contentHashCode()
        result = 31 * result + documentType.hashCode()
        result = 31 * result + historicalPatterns.hashCode()
        return result
    }
}

@Serializable
data class InvoiceExtractionResponse(
    val vendor: String,
    val invoiceNumber: String,
    val date: String,
    val totalAmount: Double,
    val currency: String,
    val lineItems: List<LineItem>,
    val suggestedAccounts: Map<String, AccountSuggestion>,
    val confidence: Double
)

@Serializable
data class LineItem(
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val taxRate: Double? = null
)