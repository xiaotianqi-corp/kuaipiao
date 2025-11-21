package org.xiaotianqi.kuaipiao.scripts

import org.flywaydb.core.Flyway
import org.xiaotianqi.kuaipiao.config.PostgresConfig
import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader

fun main() {
    println("↩️ Iniciando reversor de migraciones Kuaipiao...")

    ConfigurationManager(ConfigurationManager.DEFAULT_CONFIG_PACKAGE, ConfigurationReader::read).initialize()

    val flyway = Flyway.configure()
        .dataSource(PostgresConfig.url, PostgresConfig.user, PostgresConfig.password)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .cleanDisabled(false)
        .load()

    val info = flyway.info()
    val applied = info.applied()

    println("\n📦 Migraciones aplicadas (${applied.size}):")
    if (applied.isEmpty()) {
        println("  - Ninguna migración aplicada. Nada que revertir.")
        return
    }

    applied.forEach {
        println("  - ✅ ${it.version} | ${it.description} (${it.installedOn})")
    }

    val current = info.current()
    if (current == null) {
        println("\n⚠️ No hay migración actual para revertir.")
        return
    }

    println("\nÚltima migración aplicada: ${current.version} | ${current.description}")

    val autoRollback = System.getenv("AUTO_ROLLBACK")?.lowercase() != "false"

    if (!autoRollback) {
        println("\n🛑 AUTO_ROLLBACK=false detectado. Rollback detenido manualmente.")
        return
    }

    println("\n⚙️ Ejecutando rollback automáticamente (modo OSS: clean + migrate)...")

    try {
        flyway.clean()

        println("\n✅ Base de datos reiniciada correctamente.")
    } catch (e: Exception) {
        println("\n❌ Error al ejecutar rollback: ${e.message}")
        e.printStackTrace()
    }

    val after = flyway.info().current()
    println("\n📖 Estado actual:")
    println("   Versión actual: ${after?.version ?: "Ninguna (vacía)"}")
}
