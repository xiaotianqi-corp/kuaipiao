package org.xiaotianqi.kuaipiao.core.clients.ai

import io.ktor.client.*
import org.xiaotianqi.kuaipiao.config.ai.*
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import org.xiaotianqi.kuaipiao.core.ports.ProductClassificationService
import org.xiaotianqi.kuaipiao.domain.invoice.InvoiceProcessingResult
import org.xiaotianqi.kuaipiao.enums.FileType
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalTime
@ExperimentalUuidApi
object AiClientFactory {

    enum class Provider {
        OPENAI, DEEPSEEK, ANTHROPIC, GOOGLE_VISION
    }

    fun create(
        provider: Provider,
        httpClient: HttpClient,
        configs: Map<Provider, Any>
    ): InvoiceExtractionService {
        return when (provider) {
            Provider.OPENAI -> OpenAIClientAdapter(
                OpenAIClient(httpClient, configs[Provider.OPENAI] as OpenAIConfig)
            )

            Provider.DEEPSEEK -> DeepSeekClientAdapter(
                DeepSeekClient(httpClient, configs[Provider.DEEPSEEK] as DeepSeekConfig)
            )

            Provider.ANTHROPIC -> object : InvoiceExtractionService {
                override suspend fun processInvoice(fileBytes: ByteArray, fileType: FileType, country: String) =
                    InvoiceProcessingResult.mock("Anthropic - Not implemented for invoices")
            }

            Provider.GOOGLE_VISION -> object : InvoiceExtractionService {
                override suspend fun processInvoice(fileBytes: ByteArray, fileType: FileType, country: String) =
                    InvoiceProcessingResult.mock("GoogleVision - OCR only")
            }
        }
    }

    fun createClassifier(
        provider: Provider,
        httpClient: HttpClient,
        configs: Map<Provider, Any>
    ): ProductClassificationService {
        return when (provider) {
            Provider.OPENAI ->
                OpenAIProductClassifierAdapter(
                    OpenAIProductClassifier(
                        httpClient,
                        configs[Provider.OPENAI] as OpenAIConfig
                    )
                )

            Provider.DEEPSEEK ->
                DeepSeekProductClassifierAdapter(
                    DeepSeekProductClassifier(
                        httpClient,
                        configs[Provider.DEEPSEEK] as DeepSeekConfig
                    )
                )

            Provider.ANTHROPIC ->
                AnthropicProductClassifierAdapter(
                    AnthropicProductClassifier(
                        httpClient,
                        configs[Provider.ANTHROPIC] as AnthropicConfig
                    )
                )

            Provider.GOOGLE_VISION ->
                GoogleVisionProductClassifierAdapter(
                    GoogleVisionProductClassifier(
                        httpClient,
                        configs[Provider.GOOGLE_VISION] as GoogleVisionConfig
                    )
                )
        }
    }
}
