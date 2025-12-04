package org.xiaotianqi.kuaipiao.scripts

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.xiaotianqi.kuaipiao.config.PostgresConfig
import org.xiaotianqi.kuaipiao.config.core.ConfigurationManager
import org.xiaotianqi.kuaipiao.config.core.ConfigurationReader
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.organization.OrganizationsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseAuditLogTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseBackupsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterpriseMigrationsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.EmailVerificationTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.PasswordResetTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UsersTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.enterprise.EnterprisesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.PermissionsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RolePermissionsTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.RolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.rbac.UserRolesTable
import org.xiaotianqi.kuaipiao.data.sources.db.schemas.user.UserOrganizationsTable
import org.xiaotianqi.kuaipiao.scripts.core.createMigrationsFolderIfNotExisting
import org.xiaotianqi.kuaipiao.scripts.core.formatSql
import java.io.File

private const val DB_DRIVER = "org.postgresql.Driver"

fun main() {
    println("🚀 Generando migraciones modulares...")

    ConfigurationManager(ConfigurationManager.DEFAULT_CONFIG_PACKAGE, ConfigurationReader::read).initialize()

    Database.connect(
        url = PostgresConfig.url,
        driver = DB_DRIVER,
        user = PostgresConfig.user,
        password = PostgresConfig.password
    )

    val modules = mapOf(
        "enterprise_table" to listOf(
            EnterprisesTable,
            EnterpriseMigrationsTable,
            EnterpriseBackupsTable,
            EnterpriseAuditLogTable,
        ),
        "organization_table" to listOf(
            OrganizationsTable
        ),
        "user_table" to listOf(
            UsersTable,
            PasswordResetTable,
            EmailVerificationTable,
            UserOrganizationsTable,
        ),
        "rbac_table" to listOf(
            RolesTable,
            PermissionsTable,
            RolePermissionsTable,
            UserRolesTable,
        )
    )

    val folder = createMigrationsFolderIfNotExisting()

    val currentVersion = getLastMigrationVersion(folder)
    var nextVersion = currentVersion + 1

    modules.forEach { (moduleName, tables) ->
        val statements = transaction {
            SchemaUtils.createStatements(*tables.toTypedArray())
        }

        val filteredStatements = statements.filterNot { sql ->
            (moduleName != "enterprise_table" && sql.contains("CREATE TABLE IF NOT EXISTS enterprises")) ||
            (moduleName == "user_table" && sql.contains("CREATE TABLE IF NOT EXISTS organizations")) ||
            (moduleName == "rbac_table" && sql.contains("CREATE TABLE IF NOT EXISTS users"))||
            (moduleName != "user_table" && sql.contains("ALTER TABLE users", ignoreCase = true))
        }

        val formattedSql = formatSql(filteredStatements.joinToString("\n\n") { "$it;" })
        val fileName = "V${nextVersion}__${moduleName}.sql"
        val file = File(folder, fileName)
        file.writeText(formattedSql)

        println("✅ Migración creada: ${file.name} (${tables.size} tablas)")
        nextVersion++
    }

    println("🎉 Todas las migraciones fueron generadas exitosamente en: ${folder.absolutePath}")
}

private fun getLastMigrationVersion(folder: File): Int {
    val regex = Regex("""V(\d+)__.*\.sql""")
    val versions = folder.listFiles()
        ?.mapNotNull { file -> regex.find(file.name)?.groupValues?.get(1)?.toIntOrNull() }
        ?: emptyList()
    return versions.maxOrNull() ?: 0
}
