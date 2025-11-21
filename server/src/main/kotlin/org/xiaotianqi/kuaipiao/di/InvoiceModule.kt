package org.xiaotianqi.kuaipiao.di

import org.koin.dsl.module
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.xiaotianqi.kuaipiao.config.ai.DeepSeekConfig
import org.xiaotianqi.kuaipiao.core.clients.ai.DeepSeekClient
import org.xiaotianqi.kuaipiao.core.clients.ai.DeepSeekClientAdapter
import org.xiaotianqi.kuaipiao.core.logic.usecases.ai.ProcessInvoiceUseCase
import org.xiaotianqi.kuaipiao.core.ports.InvoiceExtractionService
import kotlin.time.ExperimentalTime

@ExperimentalTime
val invoiceModule = module {

    single { HttpClient(CIO) }
    single { DeepSeekConfig }
    single { DeepSeekClient(get(), get(), debugLogs = true) }
    single<InvoiceExtractionService> { DeepSeekClientAdapter(get()) }
    single { ProcessInvoiceUseCase(get()) }
}
