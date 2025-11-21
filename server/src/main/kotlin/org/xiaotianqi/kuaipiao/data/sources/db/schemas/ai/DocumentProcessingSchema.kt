package org.xiaotianqi.kuaipiao.data.sources.db.schemas.ai

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object DocumentProcessingTable : UUIDTable("document_processing") {
    val userId = varchar("user_id", 36).index()
    val companyId = varchar("company_id", 36).index()
    val fileType = varchar("file_type", 20)
    val documentType = varchar("document_type", 50)
    val fileName = varchar("file_name", 255)
    val fileSize = long("file_size")
    val confidence = double("confidence")
    val processingTime = long("processing_time")
    val aiProvider = varchar("ai_provider", 50)
    val extractedFields = integer("extracted_fields")
    val status = varchar("status", 20)
    val errorMessage = text("error_message").nullable()
    val createdAt = timestamp("created_at")
}

object InvoiceDataTable : UUIDTable("invoice_processing_data") {
    val processingId = reference("processing_id", DocumentProcessingTable, onDelete = ReferenceOption.CASCADE)
    val invoiceNumber = varchar("invoice_number", 100)
    val issueDate = varchar("issue_date", 20)
    val emitterRuc = varchar("emitter_ruc", 20)
    val receiverRuc = varchar("receiver_ruc", 20)
    val subtotal = decimal("subtotal", 10, 2)
    val total = decimal("total", 10, 2)
    val currency = varchar("currency", 10)
    val itemsCount = integer("items_count")
    val taxesAmount = decimal("taxes_amount", 10, 2)
    val rawText = text("raw_text").nullable()
}

class DocumentProcessingEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DocumentProcessingEntity>(DocumentProcessingTable)
    var userId by DocumentProcessingTable.userId
    var companyId by DocumentProcessingTable.companyId
    var fileType by DocumentProcessingTable.fileType
    var documentType by DocumentProcessingTable.documentType
    var fileName by DocumentProcessingTable.fileName
    var fileSize by DocumentProcessingTable.fileSize
    var confidence by DocumentProcessingTable.confidence
    var processingTime by DocumentProcessingTable.processingTime
    var aiProvider by DocumentProcessingTable.aiProvider
    var extractedFields by DocumentProcessingTable.extractedFields
    var status by DocumentProcessingTable.status
    var errorMessage by DocumentProcessingTable.errorMessage
    var createdAt by DocumentProcessingTable.createdAt
}

class InvoiceDataEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InvoiceDataEntity>(InvoiceDataTable)
    var processing by DocumentProcessingEntity referencedOn InvoiceDataTable.processingId
    var invoiceNumber by InvoiceDataTable.invoiceNumber
    var issueDate by InvoiceDataTable.issueDate
    var emitterRuc by InvoiceDataTable.emitterRuc
    var receiverRuc by InvoiceDataTable.receiverRuc
    var subtotal by InvoiceDataTable.subtotal
    var total by InvoiceDataTable.total
    var currency by InvoiceDataTable.currency
    var itemsCount by InvoiceDataTable.itemsCount
    var taxesAmount by InvoiceDataTable.taxesAmount
    var rawText by InvoiceDataTable.rawText
}