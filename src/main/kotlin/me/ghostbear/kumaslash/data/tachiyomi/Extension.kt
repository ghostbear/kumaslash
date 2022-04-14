package me.ghostbear.kumaslash.data.tachiyomi

import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlinx.serialization.Serializable

@Serializable
data class Extension(
    val name: String,
    val pkg: String,
    val apk: String,
    val lang: String,
    val code: Long,
    val version: String,
    val nsfw: Long,
    val hasReadme: Long,
    val hasChangelog: Long,
    val sources: List<Source>
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
