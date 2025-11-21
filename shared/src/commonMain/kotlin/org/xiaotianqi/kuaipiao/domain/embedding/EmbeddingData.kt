package org.xiaotianqi.kuaipiao.domain.embedding

import kotlinx.serialization.Serializable

@Serializable
data class EmbeddingRequest(
    val model: String,
    val input: String
)

@Serializable
data class EmbeddingResponse(
    val data: List<EmbeddingData>
)

@Serializable
data class EmbeddingData(
    val embedding: List<Double>,
    val index: Int
)
