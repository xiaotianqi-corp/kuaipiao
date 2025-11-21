package org.xiaotianqi.kuaipiao.scripts.core

import java.io.File

fun createMigrationsFolderIfNotExisting(): File {
    val folder = File("src/main/resources/db/migration")
    if (!folder.exists()) {
        folder.mkdirs()
        println("📁 Carpeta de migraciones creada en: ${folder.absolutePath}")
    }
    return folder
}