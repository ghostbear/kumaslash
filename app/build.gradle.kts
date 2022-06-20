plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(libs.kord.core)
    implementation(project(":data"))
    implementation(libs.kotlinx.serialization)
    implementation(libs.slf4j.simple)
    implementation("com.github.haroldadmin:opengraphKt:1.0.0")
}

application {
    mainClass.set("me.ghostbear.kumaslash.KumaSlashApplicationKt")
}

