package me.ghostbear.kumaslash.util

object Env {

    val guildId by lazy { System.getenv("KUMASLASH_GUILD_ID")?.toLongOrNull() }
    val debug by lazy { System.getenv("KUMASLASH_DEBUG")?.toBooleanStrictOrNull() }
    val url by lazy { System.getenv("SERVER_URL").takeIf { it.isNullOrEmpty().not() } ?: "localhost" }
    val port by lazy { System.getenv("SERVER_PORT").takeIf { it.isNullOrEmpty().not() } ?: "8080" }
    val botToken by lazy { System.getenv("BOT_TOKEN").takeIf { it.isNullOrEmpty().not() } }
}