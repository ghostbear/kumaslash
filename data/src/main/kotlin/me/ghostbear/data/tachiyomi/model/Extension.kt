package me.ghostbear.data.tachiyomi.model

import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Extension(
    @SerialName("name") val name: String,
    @SerialName("pkg") val pkg: String,
    @SerialName("apk") val apk: String,
    @SerialName("lang") val language: String,
    @SerialName("code") val code: Int,
    @SerialName("version") val version: String,
    @SerialName("nsfw") val nsfw: Int,
    @SerialName("hasReadme") val hasReadme: Int,
    @SerialName("hasChangelog") val hasChangelog: Int,
    @SerialName("sources") val sources: List<Source>,
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
