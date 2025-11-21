package org.xiaotianqi.kuaipiao.domain.document

import kotlinx.serialization.Serializable

@Serializable
data class OcrResult(
    val extractedText: String,
    val confidence: Double,
    val pages: List<OcrPage>
)

@Serializable
data class OcrPage(
    val pageNumber: Int,
    val text: String,
    val confidence: Double
)
