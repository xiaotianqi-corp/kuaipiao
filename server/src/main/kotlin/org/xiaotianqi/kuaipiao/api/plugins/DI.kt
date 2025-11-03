package org.xiaotianqi.kuaipiao.api.plugins

import org.xiaotianqi.kuaipiao.config.ApplicationConfig
import org.xiaotianqi.kuaipiao.di.ClientModule
import org.xiaotianqi.kuaipiao.di.DataModule
import org.xiaotianqi.kuaipiao.di.IClosableComponent
import org.xiaotianqi.kuaipiao.di.LogicModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger
import org.koin.core.logger.Level
import org.koin.ktor.ext.getKoin

private val logger = KotlinLogging.logger { }

/**
 * Configures dependency injection and graceful shutdown for multiplatform Ktor
 */
fun Application.configureDI() {
    install(Koin) {
        slf4jLogger(Level.valueOf(ApplicationConfig.logLevel.levelStr))

        // Definimos los módulos manualmente
        modules(
            module {
                // LogicModule bindings
                single { LogicModule() }
            },
            module {
                // ClientModule bindings
                single { ClientModule() }
            },
            module {
                // DataModule bindings
                single { DataModule() }
            }
        )

        this.createEagerInstances()
    }

    monitor.subscribe(KoinApplicationStarted) {
        logger.info { "Koin application started" }
    }

    monitor.subscribe(KoinApplicationStopPreparing) {
        logger.info { "Shutdown started" }

        val closableComponents by lazy {
            getKoin().getAll<IClosableComponent>()
        }

        closableComponents.forEach {
            runBlocking {
                it.close()
            }
        }
    }

    monitor.subscribe(KoinApplicationStopped) {
        logger.info { "Shutdown completed gracefully" }
    }
}
