plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.21"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}