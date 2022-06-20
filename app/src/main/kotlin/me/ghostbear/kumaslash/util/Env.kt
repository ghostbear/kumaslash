package me.ghostbear.kumaslash.util

object Env {

    val guildId by lazy { System.getenv("KUMASLASH_GUILD_ID")?.toLongOrNull() }
    val debug by lazy { System.getenv("KUMASLASH_DEBUG")?.toBooleanStrictOrNull() }

}