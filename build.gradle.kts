import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20"
    application
}

group = "me.ghostbear"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.8.0-M12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    val ktor_version= "2.0.0"
    // implementation("io.ktor:ktor-client-core:$ktor_version")
    // implementation("io.ktor:ktor-client-cio:$ktor_version")
    // implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    // implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("KumaSlashApplicationKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "me.ghostbear.kumaslash.KumaSlashApplicationKt"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}