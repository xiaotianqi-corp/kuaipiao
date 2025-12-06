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
import org.xiaotianqi.kuaipiao.config.ApiConfig
import org.xiaotianqi.kuaipiao.core.clients.ResendClient
import org.xiaotianqi.kuaipiao.core.security.JwtModule
import org.xiaotianqi.kuaipiao.di.AuthModule
import org.xiaotianqi.kuaipiao.di.SecurityModule
import org.xiaotianqi.kuaipiao.di.dataModuleDeclarations

private val logger = KotlinLogging.logger { }

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger(Level.valueOf(ApplicationConfig.logLevel.levelStr))

        modules(
            module {
                single { ApiConfig }
            },
            module {
                single { LogicModule() }
            },
            module {
                single { ClientModule() }
            },
            module {
                single { ResendClient() }
            },
            dataModuleDeclarations,
            module {
                single { AuthModule() }
            },
            module {
                single { JwtModule() }
            },
            SecurityModule
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
