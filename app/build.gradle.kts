plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(libs.kord.core)
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(libs.kotlinx.serialization)
    implementation(libs.slf4j.simple)
}

application {
    mainClass.set("me.ghostbear.kumaslash.KumaSlashApplicationKt")
}

