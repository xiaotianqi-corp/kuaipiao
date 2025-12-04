package org.xiaotianqi.kuaipiao.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.Database

private val logger = KotlinLogging.logger {}

object DatabaseModule {
    fun initialize() {
        try {
            Database.connect(
                url = PostgresConfig.url,
                driver = "org.postgresql.Driver",
                user = PostgresConfig.user,
                password = PostgresConfig.password
            )
            logger.info { "Database connected successfully" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to connect to database" }
            throw e
        }
    }
}