package org.xiaotianqi.kuaipiao

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory
import org.xiaotianqi.kuaipiao.api.plugins.AuthorizationPlugin
import org.xiaotianqi.kuaipiao.api.plugins.configureDI
import org.xiaotianqi.kuaipiao.api.plugins.configureHTTP
import org.xiaotianqi.kuaipiao.api.plugins.configureOpenAPI
import org.xiaotianqi.kuaipiao.api.plugins.configureSecurity
import org.xiaotianqi.kuaipiao.api.plugins.configureSerialization
import org.xiaotianqi.kuaipiao.api.plugins.configureStatusPages
import org.xiaotianqi.kuaipiao.api.plugins.configureValidator
import org.xiaotianqi.kuaipiao.api.routing.configureRouting
import org.xiaotianqi.kuaipiao.config.ApiConfig
import org.xiaotianqi.kuaipiao.config.ApplicationConfig
import org.xiaotianqi.kuaipiao.config.DatabaseModule
import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
    println("""
     __  ___  __    __       ___       __  .______    __       ___       ______   
    |  |/  / |  |  |  |     /   \     |  | |   _  \  |  |     /   \     /  __  \  
    |  '  /  |  |  |  |    /  ^  \    |  | |  |_)  | |  |    /  ^  \   |  |  |  | 
    |    <   |  |  |  |   /  /_\  \   |  | |   ___/  |  |   /  /_\  \  |  |  |  | 
    |  .  \  |  `--'  |  /  _____  \  |  | |  |      |  |  /  _____  \ |  `--'  | 
    |__|\__\  \______/  /__/     \__\ |__| | _|      |__| /__/     \__\ \______/  
                                                                                  
    """.trimIndent())

    val configInitializer = ConfigurationManager(
        packageName = ConfigurationManager.DEFAULT_CONFIG_PACKAGE,
        configurationReader = ConfigurationReader::read
    )

    configInitializer.initialize()

    /**
     * Configure logging
     */
    (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = ApplicationConfig.logLevel

    /**
     * Start api server
     */
    embeddedServer(CIO, port = ApiConfig.port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@ExperimentalTime
fun Application.module(testing: Boolean = false) {
    if (!testing) {
        DatabaseModule.initialize()
    }
    install(AuthorizationPlugin)
    configureOpenAPI()
    configureDI()
    configureSerialization()
    configureHTTP()
    if (!testing) {
        configureSecurity()
    }
    configureStatusPages()
    configureValidator()
    configureRouting()
}