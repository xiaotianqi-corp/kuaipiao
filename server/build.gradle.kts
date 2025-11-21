@file:OptIn(OpenApiPreview::class)

import io.ktor.plugin.OpenApiPreview

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
    application
}

group = "org.xiaotianqi.kuaipiao"
version = "1.0.0"
application {
    mainClass.set("org.xiaotianqi.kuaipiao.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")

    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.development=$isDevelopment",
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"
    )
}

ktor {
    @OptIn(OpenApiPreview::class)
    openApi {
        title = "KuaiPiao OpenApi"
        version = "1.0.0"
        summary = "This is a Kuaipiao API by Xiaotianqi"
        description = "This is a longer description"
        termsOfService = "https://xiaotianqi.com/kuaipiao/terms/"
        contact = "contact@xiaotianqi.com"
        license = "Apache/1.0"

        target = project.layout.buildDirectory.file("open-api.json")
    }
}

dependencies {
    implementation(libs.dotenv)
    implementation(projects.shared)
    implementation(libs.bundles.monitoring)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.ktor.client.cio)
    implementation(libs.koin.ktor.annotations)
    implementation(libs.bundles.di)
    implementation(libs.bundles.ai)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.swagger)
    implementation(libs.bundles.validation)
    implementation(libs.bundles.messaging)
    implementation(libs.bundles.google)
    implementation(libs.bundles.calendar)
    implementation(libs.bundles.resilience4j)
    implementation(libs.bundles.database)
    implementation(libs.bundles.caching)
    implementation(libs.bundles.security)
    implementation(libs.bundles.graphql)
    implementation(libs.bundles.xmlx)
    testImplementation(libs.bundles.testing)
}


tasks.register<JavaExec>("makeMigration") {
    group = "database"
    description = "Generate initial PostgreSQL migration script"
    mainClass.set("org.xiaotianqi.kuaipiao.scripts.GenerateFirstPostgresMigrationKt")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("migrate") {
    group = "database"
    description = "Ejecuta las migraciones pendientes en la base de datos"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.xiaotianqi.kuaipiao.scripts.RunMigrationsKt")
}

tasks.register<JavaExec>("migrateRollback") {
    group = "database"
    description = "Revierte la última migración aplicada (requiere confirmación)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.xiaotianqi.kuaipiao.scripts.RollbackMigrationsKt")
}