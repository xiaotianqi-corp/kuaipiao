package org.xiaotianqi.kuaipiao

import ch.qos.logback.classic.Logger
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import org.xiaotianqi.kuaipiao.api.plugins.configureDI
import org.xiaotianqi.kuaipiao.api.plugins.configureHTTP
import org.xiaotianqi.kuaipiao.api.plugins.configureSecurity
import org.xiaotianqi.kuaipiao.api.plugins.configureSerialization
import org.xiaotianqi.kuaipiao.api.plugins.configureStatusPages
import org.xiaotianqi.kuaipiao.api.plugins.configureValidator
import org.xiaotianqi.kuaipiao.api.routing.configureRouting
import org.xiaotianqi.kuaipiao.config.ApiConfig
import org.xiaotianqi.kuaipiao.config.ApplicationConfig
import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader

fun main() {
    println("""
         _____     ______        ______   ______     ______    
        /\  __-.  /\  __ \      /\__  _\ /\  __ \   /\  __ \   
        \ \ \/\ \ \ \ \/\ \     \/_/\ \/ \ \ \/\ \  \ \ \/\ \  
         \ \____-  \ \_____\       \ \_\  \ \_____\  \ \_____\ 
          \/____/   \/_____/        \/_/   \/_____/   \/_____/ 
                                                       
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
    embeddedServer(Netty, port = ApiConfig.port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureDI()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureStatusPages()
    configureValidator()
    configureRouting()
}