plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.21"
    id("com.squareup.sqldelight")
}

dependencies {
    implementation(project(":common"))

    implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Ktor Server
    val ktorVersion = "2.0.2"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")

    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")

    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    // Ktor Client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

application {
    mainClass.set("me.ghostbear.web.MainKt")
}

sqldelight {
    database("Database") {
        packageName = "me.ghostbear.database"
        dialect = "sqlite:3.25"
    }
}
