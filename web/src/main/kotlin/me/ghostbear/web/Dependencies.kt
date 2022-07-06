package me.ghostbear.web

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.ghostbear.database.Database

val json = Json {
    prettyPrint = true
    isLenient = true
}

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
    }
}

val driver by lazy {
    val driver = JdbcSqliteDriver("jdbc:sqlite:kumaslash.db")
    Database.Schema.create(driver)
    driver
}

val database = Database(driver)