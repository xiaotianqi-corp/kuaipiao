package org.xiaotianqi.kuaipiao.domain.ai

import kotlinx.serialization.Serializable
import org.xiaotianqi.kuaipiao.enums.FeatureType

@Serializable
data class GoogleVisionRequest(
    val imageData: ByteArray,
    val features: List<FeatureType> = listOf(FeatureType.TEXT_DETECTION)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GoogleVisionRequest
        if (!imageData.contentEquals(other.imageData)) return false
        if (features != other.features) return false
        return true
    }

    override fun hashCode(): Int {
        var result = imageData.contentHashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}

@Serializable
data class GoogleVisionResponse(
    val textAnnotations: List<TextAnnotation>,
    val fullText: String? = null
)

@Serializable
data class TextAnnotation(
    val text: String,
    val description: String,
    val boundingBox: BoundingBox? = null,
    val confidence: Double? = null
)

@Serializable
data class BoundingBox(
    val vertices: List<Vertex>
)

@Serializable
data class Vertex(
    val x: Int,
    val y: Int
)

@Serializable
data class VisionRequest(
    val requests: List<ImageRequest>
)

@Serializable
data class ImageRequest(
    val image: ImageContent,
    val features: List<Feature>
)

@Serializable
data class ImageContent(
    val bytes: String? = null,
    val content: String? = null
)

@Serializable
data class Feature(
    val type: String,
    val maxResults: Int = 10
)

@Serializable
data class VisionResponse(
    val responses: List<VisionAnnotateImageResponse>
)

@Serializable
data class VisionAnnotateImageResponse(
    val fullTextAnnotation: FullTextAnnotation? = null,
    val textAnnotations: List<EntityAnnotation>? = null,
    val labelAnnotations: List<EntityAnnotation>? = null,
    val logoAnnotations: List<EntityAnnotation>? = null,
    val error: VisionError? = null
)

@Serializable
data class FullTextAnnotation(
    val text: String,
    val pages: List<Page> = emptyList()
)

@Serializable
data class EntityAnnotation(
    val description: String,
    val score: Float? = null,
    val confidence: Float? = null,
    val boundingPoly: BoundingPoly? = null
)

@Serializable
data class BoundingPoly(
    val vertices: List<Vertex> = emptyList()
)

@Serializable
data class VisionError(
    val code: Int,
    val message: String
)

@Serializable
data class Page(
    val width: Int? = null,
    val height: Int? = null,
    val blocks: List<Block>? = null
)

@Serializable
data class Block(
    val blockType: String? = null,
    val confidence: Float? = null,
    val boundingBox: BoundingBox? = null
)

@Serializable
data class DocumentStructureAnalysis(
    val fullText: String,
    val labels: List<String>,
    val logos: List<String>,
    val pages: List<Page>
)

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.1,
    val topK: Int = 40,
    val topP: Double = 0.95,
    val maxOutputTokens: Int = 8192
)

@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)