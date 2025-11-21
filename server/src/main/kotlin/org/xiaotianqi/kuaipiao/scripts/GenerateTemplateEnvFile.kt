package org.xiaotianqi.kuaipiao.scripts

import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader
import org.xiaotianqi.kuaipiao.scripts.core.createMigrationsFolderIfNotExisting
import java.io.File

fun main() {
    val configs = ConfigurationManager(
        packageName = ConfigurationManager.DEFAULT_CONFIG_PACKAGE,
        configurationReader = ConfigurationReader::read
    ).listConfigurations()

    val folder = createMigrationsFolderIfNotExisting()
    val file = File(folder, ".env.template")
    file.writeText(configs.joinToString("\n") { it.toString() } )
}