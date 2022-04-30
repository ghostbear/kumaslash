package me.ghostbear.data.tachiyomi

import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlinx.serialization.Serializable

@Serializable
data class Extension(
    val name: String = "",
    val pkg: String = "",
    val apk: String = "",
    val lang: String = "",
    val code: Long = -1,
    val version: String = "",
    val nsfw: Long = -1,
    val hasReadme: Long = -1,
    val hasChangelog: Long = -1,
    val sources: List<Source> = listOf()
)

fun Extension.toMessage(): InteractionResponseModifyBuilder.() -> Unit = {
    content = "<:nadeshikosuccess:843281745088217158> Source was found in the **${name.replace("Tachiyomi: ", "")}** extension."
    components = (components ?: mutableListOf())
    components!!.add(
        ActionRowBuilder().apply {
            linkButton("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/$apk") {
                label = "Download"
            }
        }
    )
}
