package org.xiaotianqi.kuaipiao.di

import org.xiaotianqi.kuaipiao.config.ai.*
import org.xiaotianqi.kuaipiao.core.clients.*
import org.xiaotianqi.kuaipiao.core.logic.ai.*
import org.xiaotianqi.kuaipiao.data.daos.ai.*
import org.xiaotianqi.kuaipiao.data.validation.AiResponseValidator
import io.ktor.server.application.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import org.koin.dsl.module
import org.xiaotianqi.kuaipiao.core.clients.ai.AnthropicClient
import org.xiaotianqi.kuaipiao.core.clients.ai.DeepSeekClient
import org.xiaotianqi.kuaipiao.core.clients.ai.GoogleVisionClient
import org.xiaotianqi.kuaipiao.core.clients.ai.OpenAIClient
import org.xiaotianqi.kuaipiao.core.logic.usecases.ClassifyTariffCodeUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.ai.AnalyzeComplianceRiskUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.ai.ProcessInvoiceUseCase
import org.xiaotianqi.kuaipiao.core.logic.usecases.ai.ReconcileAccountingUseCase
import org.xiaotianqi.kuaipiao.data.sources.cache.cm.ai.AiCacheSource
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.AiDBI
import org.xiaotianqi.kuaipiao.data.sources.db.dbi.ai.impl.AiDBIImpl
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalTime
@ExperimentalUuidApi
@ExperimentalStdlibApi
@ExperimentalLettuceCoroutinesApi
fun Application.AiModule() = module {

    single { OpenAIConfig }
    single { DeepSeekConfig }
    single { GoogleVisionConfig }
    single { AnthropicConfig }
    single { TimeoutConfig }

    single {
        OpenAIClient(get(), OpenAIConfig)
    }

    single {
        DeepSeekClient(get(), DeepSeekConfig)
    }

    single {
        GoogleVisionClient(get(), GoogleVisionConfig)
    }

    single {
        AnthropicClient(get(), AnthropicConfig)
    }

    single {
        AiClientManager(
            openAIClient = get(),
            deepSeekClient = get(),
            googleVisionClient = get(),
            anthropicClient = get(),
            timeout = TimeoutConfig.perProviderMs.seconds
        )
    }

    single {
        AiOrchestrator(
            aiClientManager = get(),
            googleVisionClient = get(),
            cache = get(),
            countryRuleEngine = get(),
            taxComplianceAnalyzer = get(),
            benchmarkDao = get(),
            companyDao = get()
        )
    }

    single { CountryRuleEngine() }
    single {
        TaxComplianceAnalyzer(
            companyDao = get(),
            benchmarkDao = get()
        )
    }
    single { ResponseValidator() }

    single {
        ProcessInvoiceUseCase(
            extractionService = get<AiClientManager>()
        )
    }

    single {
        ClassifyTariffCodeUseCase(
            aiClientManager = get(),
            countryRuleEngine = get(),
            aiDBI = get(),
            aiCacheSource = get(),
            httpClient = get(),
            openAIConfig = OpenAIConfig
        )
    }

    single {
        AnalyzeComplianceRiskUseCase(
            aiOrchestrator = get(),
            taxComplianceAnalyzer = get(),
            aiDBI = get()
        )
    }

    single {
        ReconcileAccountingUseCase(
            aiOrchestrator = get(),
            validator = get(),
            aiDBI = get()
        )
    }

    // Data sources
    single {
        AiCacheSource(
            redisClient = get(),
            aiCacheDao = get()
        )
    }

    single<AiDBI> {
        AiDBIImpl(
            documentProcessingDao = get(),
            complianceRiskDao = get(),
            aiCacheDao = get(),
            modelResultDao = get()
        )
    }

    // DAOs
    single { DocumentProcessingDao() }
    single { ComplianceRiskDao() }
    single { AiCacheDao() }
    single { ModelResultDao() }

    // Validación
    single { AiResponseValidator() }
}