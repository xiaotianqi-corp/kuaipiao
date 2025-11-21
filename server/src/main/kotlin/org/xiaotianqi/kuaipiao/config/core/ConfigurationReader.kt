package org.xiaotianqi.kuaipiao.config.core

import ch.qos.logback.classic.Level
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import io.github.cdimascio.dotenv.dotenv
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import kotlin.reflect.KClass

private val log = KotlinLogging.logger { }

object ConfigurationReader {

    private val dotenv: Dotenv? = loadDotenv()

    private fun loadDotenv(): Dotenv? {
        return try {
            val localEnv = File(".env")
            val rootEnv = File("../.env")

            when {
                localEnv.exists() -> {
                    log.info { "✅ Loaded .env from: ${localEnv.absolutePath}" }
                    dotenv {
                        directory = "."
                        filename = ".env"
                    }
                }

                rootEnv.exists() -> {
                    log.info { "✅ Loaded .env from: ${rootEnv.absolutePath}" }
                    dotenv {
                        directory = ".."
                        filename = ".env"
                    }
                }

                else -> {
                    log.warn { "⚠️  .env file not found, using System environment variables" }
                    null
                }
            }
        } catch (e: DotenvException) {
            log.warn { "⚠️  Error loading .env file: ${e.message}" }
            null
        }
    }

    fun read(key: String, type: KClass<*>): Any? {
        val value: String? = dotenv?.get(key) ?: System.getenv(key)

        return try {
            when (type) {
                String::class -> value
                Int::class -> value?.toInt()
                Long::class -> value?.toLong()
                Boolean::class -> value?.toBoolean()
                Level::class -> {
                    value?.uppercase()?.let { Level.valueOf(it) }
                }
                else -> throw UnsupportedOperationException(
                    "Configuration reader required to read a value of type $type for key '$key' but no casting is implemented for that type"
                )
            }
        } catch (e: NumberFormatException) {
            log.error { "Could not cast a value with key '$key' in configuration reader" }
            throw e
        }
    }
}
