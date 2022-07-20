package me.ghostbear.kumaslash.util

object Env {

    val guildId by lazy { System.getenv("KUMASLASH_GUILD_ID")?.toLongOrNull() }
    val debug by lazy { System.getenv("KUMASLASH_DEBUG")?.toBooleanStrictOrNull() }
    val url by lazy { System.getenv("KUMASLASH_BACKEND_URL").takeIf { it.isNullOrEmpty().not() } ?: "localhost" }
    val port by lazy { System.getenv("KUMASLASH_BACKEND_PORT").takeIf { it.isNullOrEmpty().not() } ?: "8080" }

}