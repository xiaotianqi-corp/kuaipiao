package org.xiaotianqi.kuaipiao.core.clients.ai

import org.xiaotianqi.kuaipiao.domain.document.DocumentExtractionResult
import org.xiaotianqi.kuaipiao.enums.FileType

interface DocumentExtractor {
    suspend fun extractDocument(
        prompt: String,
        fileBytes: ByteArray,
        fileType: FileType
    ): DocumentExtractionResult
}
