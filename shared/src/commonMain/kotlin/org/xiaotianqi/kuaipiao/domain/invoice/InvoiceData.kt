package org.xiaotianqi.kuaipiao.domain.invoice

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.customer.CustomerData
import org.xiaotianqi.kuaipiao.domain.document.DocumentData
import org.xiaotianqi.kuaipiao.enums.InvoiceStatus
import org.xiaotianqi.kuaipiao.enums.ItemType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import org.xiaotianqi.kuaipiao.enums.PaymentStatus
import org.xiaotianqi.kuaipiao.enums.TaxType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class InvoiceData(
    val id: String,
    val providerId: InvoiceBusiness,
    val customerId: CustomerData,
    val number: String,
    val date: Instant,
    val dueDate: Instant,
    val items: List<InvoiceItemData> = emptyList(),
    val subtotal: String,
    val taxes: List<InvoiceTax> = emptyList(),
    val tax: String,
    val total: String,
    val status: OperationStatus = OperationStatus.ISSUED,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val currency: String,
    val notes: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Instant = Clock.System.now(),
)

@Serializable
data class InvoiceItemData(
    val id: String,
    val code: String,
    val productId: String,
    val description: String,
    val quantity: String,
    val unitPrice: String,
    val discount: String = "0",
    val tax: String? = "0.0",
    val taxAmount: String? = "0.0",
    val subtotal: String? = "0.0",
    val total: String = "0.0",
    val unitOfMeasure: String? = null,
    val itemType: ItemType? = null
)

@Serializable
@ExperimentalTime
data class ElectronicInvoiceData(
    val invoiceId: InvoiceData,
    val accessKey: String,
    val xmlSigned: String,
    val authorizationDate: Long? = null,
    val authorizationNumber: String? = null,
    val status: InvoiceStatus
)

@Serializable
data class InvoiceTax(
    val type: TaxType,
    val rate: Double,
    val amount: Double
)

@Serializable
data class InvoiceBusiness(
    val id: String,
    val name: String,
    val country: String,
    val region: String? = null,
    val city: String? = null,
    val address: String? = null,
    val taxId: String? = null
)

@ExperimentalTime
fun InvoiceData.toDocumentData(): DocumentData {
    return DocumentData(
        documentId = this.id,
        invoiceNumber = this.number,
        issueDate = this.date.toString(),
        currency = this.currency,
        emitterRuc = this.providerId.taxId,
        emitterName = this.providerId.name,
        receiverRuc = null,
        receiverName = this.customerId.firstName,
        subtotal = this.subtotal.toDoubleOrNull(),
        taxAmount = this.tax.toDoubleOrNull(),
        total = this.total.toDoubleOrNull(),
        items = this.items,
        country = this.providerId.country,
        rawContent = null
    )
}