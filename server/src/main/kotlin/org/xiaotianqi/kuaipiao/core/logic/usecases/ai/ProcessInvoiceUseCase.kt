package org.xiaotianqi.kuaipiao.core.logic.usecases.ai

import io.github.oshai.kotlinlogging.KotlinLogging
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.domain.document.BatchProcessingResult
import org.xiaotianqi.kuaipiao.domain.document.ProcessingError
import org.xiaotianqi.kuaipiao.domain.document.ProcessingSummary
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

@ExperimentalTime
class ProcessInvoiceUseCase(
    private val extractionService: InvoiceExtractionService
) {

    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileType: FileType,
        userId: String,
        companyId: String,
        country: String
    ): Result<InvoiceProcessingResult> {

        logger.info { "Procesando factura (user=$userId, company=$companyId)" }

        return try {
            validateFile(fileBytes, fileType)

            // ✅ Procesamiento unificado a través del adapter (DeepSeekClientAdapter)
            val result = extractionService.processInvoice(
                fileBytes = fileBytes,
                fileType = fileType,
                country = country
            )

            logger.info {
                "✅ Factura procesada: ${result.invoiceId} " +
                        "(confianza=${"%.2f".format(result.confidence)})"
            }

            Result.success(result)

        } catch (e: Exception) {
            logger.error(e) { "❌ Error procesando factura" }
            Result.failure(e)
        }
    }

    suspend fun processBatch(
        files: List<Pair<ByteArray, FileType>>,
        userId: String,
        companyId: String,
        country: String
    ): BatchProcessingResult {

        val results = mutableListOf<InvoiceProcessingResult>()
        val errors = mutableListOf<ProcessingError>()

        files.forEachIndexed { index, (fileBytes, fileType) ->
            try {
                val result = invoke(fileBytes, fileType, userId, companyId, country).getOrThrow()
                results.add(result)
            } catch (e: Exception) {
                errors.add(
                    ProcessingError(
                        fileIndex = index,
                        error = e.message ?: "Error desconocido",
                        fileType = fileType
                    )
                )
            }
        }

        return BatchProcessingResult(
            successful = results,
            failed = errors,
            summary = ProcessingSummary(
                totalFiles = files.size,
                processed = results.size,
                failed = errors.size,
                averageConfidence = results.map { it.confidence }.average()
            )
        )
    }

    private fun validateFile(fileBytes: ByteArray, fileType: FileType) {
        if (fileBytes.isEmpty())
            throw IllegalArgumentException("Archivo vacío")

        if (fileBytes.size > 10 * 1024 * 1024)
            throw IllegalArgumentException("Archivo demasiado grande (>10MB)")

        when (fileType) {
            FileType.PDF -> validatePdf(fileBytes)
            FileType.IMAGE -> validateImage(fileBytes)
            FileType.EXCEL -> validateExcel(fileBytes)
            else -> throw IllegalArgumentException("Tipo de archivo no soportado: $fileType")
        }
    }

    private fun validatePdf(bytes: ByteArray) {
        val magic = bytes.copyOfRange(0, 4)
        val pdfMagic = byteArrayOf(0x25, 0x50, 0x44, 0x46) // %PDF

        if (!magic.contentEquals(pdfMagic)) {
            throw IllegalArgumentException("Archivo PDF inválido")
        }
    }

    private fun validateImage(bytes: ByteArray) {
        val jpgMagic = bytes.copyOfRange(0, 3)
        val pngMagic = bytes.copyOfRange(0, 8)

        val isJpg = jpgMagic.contentEquals(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
        val isPng = pngMagic.contentEquals(
            byteArrayOf(
                0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A.toByte(), 0x0A
            )
        )

        if (!isJpg && !isPng) {
            throw IllegalArgumentException("Formato de imagen no soportado. Use JPEG o PNG")
        }
    }

    private fun validateExcel(bytes: ByteArray) {
        val zipMagic = bytes.copyOfRange(0, 4)
        val excelMagic = byteArrayOf(0x50, 0x4B, 0x03, 0x04) // PK ZIP header

        if (!zipMagic.contentEquals(excelMagic)) {
            throw IllegalArgumentException("Formato Excel no válido")
        }
    }
}