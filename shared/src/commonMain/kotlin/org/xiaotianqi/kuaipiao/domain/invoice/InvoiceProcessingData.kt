package org.xiaotianqi.kuaipiao.domain.invoice

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.domain.customer.CustomerData
import org.xiaotianqi.kuaipiao.domain.validation.DataSuggestion
import org.xiaotianqi.kuaipiao.domain.validation.ValidationError
import org.xiaotianqi.kuaipiao.enums.BuyerDocumentType
import org.xiaotianqi.kuaipiao.enums.CustomerType
import org.xiaotianqi.kuaipiao.enums.OperationStatus
import org.xiaotianqi.kuaipiao.enums.PaymentStatus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class InvoiceProcessingResult(
    val documentId: String,
    val invoiceId: String,
    val supplierName: String,
    val totalAmount: Double,
    val currency: String,
    val issueDate: Instant,
    val taxAmount: Double,
    val extractedData: InvoiceData,
    val confidence: Double,
    val validationErrors: List<ValidationError>,
    val suggestions: List<DataSuggestion>,
    val processingTime: Long,
    val aiProvider: String,
    val status: OperationStatus,
) {
    companion object {
        fun mock(reason: String = "Mocked result"): InvoiceProcessingResult {
            val mockInvoiceData = InvoiceData(
                id = "mock-id-123",
                providerId = InvoiceBusiness(
                    id = "org-id",
                    name = "Org Name",
                    country = " "
                ),
                customerId = CustomerData(
                    id = "cust-user-uuid",
                    firstName = "org-id",
                    lastName = "Org Name",
                    customerType = CustomerType.INDIVIDUAL,
                    documentType = BuyerDocumentType.PASSPORT,
                    documentNumber = "0992037474"
                ),
                number = "INV-MOCK-001",
                date = Clock.System.now(),
                dueDate = Clock.System.now(),
                items = emptyList(),
                subtotal = "0.0",
                tax = "0.0",
                total = "0.0",
                status = OperationStatus.ISSUED,
                paymentStatus = PaymentStatus.PENDING,
                currency = "USD",
                notes = reason,
            )

            return InvoiceProcessingResult(
                documentId = "mock-doc-uuid",
                invoiceId = "mock-inv-uuid",
                supplierName = "Mock Supplier Co.",
                totalAmount = 0.0,
                currency = "USD",
                issueDate = Clock.System.now(),
                taxAmount = 0.0,
                extractedData = mockInvoiceData,
                confidence = 0.0,
                validationErrors = listOf(ValidationError("Mock Error", reason)),
                suggestions = emptyList(),
                processingTime = 0L,
                aiProvider = "MOCK",
                status = OperationStatus.ISSUED
            )
        }
    }
}