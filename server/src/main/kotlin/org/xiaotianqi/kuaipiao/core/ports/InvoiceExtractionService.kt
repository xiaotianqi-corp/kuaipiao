package org.xiaotianqi.kuaipiao.core.ports

import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface InvoiceExtractionService {
    suspend fun processInvoice(fileBytes: ByteArray, fileType: FileType, country: String): InvoiceProcessingResult
}