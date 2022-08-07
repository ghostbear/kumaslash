package me.ghostbear.web

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import data.Logging
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
    val currentVersion = driver.executeQuery(null, "PRAGMA schema_version;", 0, null)
        .getLong(0)!!.toInt()
    if (currentVersion == 0) {
        Database.Schema.create(driver)
    } else {
        val newVersion = Database.Schema.version
        if (newVersion > currentVersion) {
            Database.Schema.migrate(driver, currentVersion, newVersion)
        }
    }
    driver
}

val database = Database(
    driver,
    LoggingAdapter = Logging.Adapter(
        logAdapter = EnumColumnAdapter()
    ),
)