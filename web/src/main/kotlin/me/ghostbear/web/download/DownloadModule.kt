package me.ghostbear.web.download

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.ghostbear.common.download.Download
import me.ghostbear.common.download.DownloadParams
import me.ghostbear.common.download.Repository
import me.ghostbear.web.httpClient

sealed class GithubRepository {

    abstract val regex: Regex
    abstract val target: Pair<String, String>
    abstract val repository: Repository

    suspend fun fetch(): Download {
        val response = httpClient.get(target.toReleaseUrl())
        val body = response.body<JsonObject>()
        return Download(
            name = body["name"]?.jsonPrimitive?.content!!,
            repository = repository,
            downloadUrl = body["assets"]?.jsonArray?.find { regex.matches(it.jsonObject["name"]?.jsonPrimitive?.content!!) }?.jsonObject?.get("browser_download_url")?.jsonPrimitive?.content!!,
            changelogUrl = body["html_url"]?.jsonPrimitive?.content!!
        )
    }

    private fun Pair<String, String>.toReleaseUrl(): String {
        return "https://api.github.com/repos/$first/$second/releases/latest"
    }

    companion object {
        fun valueOf(repository: Repository): GithubRepository {
            return when (repository) {
                Repository.TACHIYOMI -> Tachiyomi()
                Repository.TACHIYOMI_SY -> TachiyomiSy()
                Repository.TACHIYOMI_J2K -> TachiyomiJ2K()
                Repository.NEKO -> Neko()
                Repository.TACHIYOMI_PREVIEW -> TachiyomiPreview()
                Repository.TACHIYOMI_SY_PREVIEW -> TachiyomiSyPreview()
            }
        }
    }
}

class Tachiyomi : GithubRepository() {
    override val regex = "^tachiyomi-v\\d+\\.\\d+\\.\\d+.apk".toRegex()
    override val target = ("tachiyomiorg" to "tachiyomi")
    override val repository = Repository.TACHIYOMI
}

class TachiyomiPreview : GithubRepository() {
    override val regex = "^tachiyomi-r\\d{4,}.apk".toRegex()
    override val target = ("tachiyomiorg" to "tachiyomi-preview")
    override val repository = Repository.TACHIYOMI_PREVIEW
}

class TachiyomiSy : GithubRepository() {
    override val regex = "TachiyomiSY[_-]\\d+\\.\\d+\\.\\d+\\.apk".toRegex()
    override val target = ("jobobby04" to "TachiyomiSY")
    override val repository = Repository.TACHIYOMI
}

class TachiyomiSyPreview : GithubRepository() {
    override val regex = "TachiyomiSY[_-]\\d{3,}.apk".toRegex()
    override val target = ("jobobby04" to "TachiyomiSYPreview")
    override val repository = Repository.TACHIYOMI_PREVIEW
}

class TachiyomiJ2K : GithubRepository() {
    override val regex = "tachiyomij2k-v\\d+\\.\\d+\\.\\d+\\.apk".toRegex()
    override val target = ("Jays2Kings" to "tachiyomiJ2K")
    override val repository = Repository.TACHIYOMI
}

class Neko : GithubRepository() {
    override val regex = "neko-universal\\.apk".toRegex()
    override val target = ("CarlosEsco" to "Neko")
    override val repository = Repository.TACHIYOMI
}

fun Application.module() {
    routing {
        get("/download") {
            try {
                val params = call.receive<DownloadParams>()
                val repository = GithubRepository.valueOf(params.repository)
                call.respond(repository.fetch())
            } catch (e: Exception) {
                println(e.message ?: "")
            }
        }
    }
}