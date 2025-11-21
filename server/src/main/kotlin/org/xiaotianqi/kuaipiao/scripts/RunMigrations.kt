package org.xiaotianqi.kuaipiao.scripts

import org.flywaydb.core.Flyway
import org.xiaotianqi.kuaipiao.config.PostgresConfig
import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader

fun main() {
    println("🚀 Iniciando gestor de migraciones Kuaipiao...")

    ConfigurationManager(ConfigurationManager.DEFAULT_CONFIG_PACKAGE, ConfigurationReader::read).initialize()

    val flyway = Flyway.configure()
        .dataSource(PostgresConfig.url, PostgresConfig.user, PostgresConfig.password)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .load()

    val info = flyway.info()
    val applied = info.applied()
    val pending = info.pending()

    println("\n📦 Migraciones aplicadas (${applied.size}):")
    if (applied.isEmpty()) {
        println("  - Ninguna aún.")
    } else {
        applied.forEach {
            println("  - ✅ ${it.version} | ${it.description} (${it.installedOn})")
        }
    }

    println("\n🕓 Migraciones pendientes (${pending.size}):")
    if (pending.isEmpty()) {
        println("  - Ninguna. La base de datos está actualizada ✅")
        return
    } else {
        pending.forEach {
            println("  - ⏳ ${it.version} | ${it.description}")
        }
    }

    val autoMigrate = System.getenv("AUTO_MIGRATE")?.lowercase() != "false"

    if (!autoMigrate) {
        println("\n🛑 AUTO_MIGRATE=false detectado. Migraciones detenidas manualmente.")
        return
    }

    println("\n⚙️ Ejecutando migraciones automáticamente...")

    val result = flyway.migrate()

    println("\n✅ Migraciones completadas con éxito.")
    println("   Nuevas migraciones ejecutadas: ${result.migrationsExecuted}")
    println("   Versión actual: ${result.targetSchemaVersion}")

    println("\n📖 Resumen:")
    flyway.info().applied().forEach {
        println("  - ${it.version} | ${it.description} (${it.installedOn}) ✅")
    }
}
