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
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.dotenv)
    implementation(projects.shared)
    implementation(libs.bundles.monitoring)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.di)
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
//    testImplementation(libs.ktor.serverTestHost)
//    testImplementation(libs.kotlin.testJunit)
//    testImplementation(libs.kotlin.test)
//    testImplementation(libs.junit)
}