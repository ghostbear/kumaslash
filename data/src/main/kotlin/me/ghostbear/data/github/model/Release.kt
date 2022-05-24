package me.ghostbear.data.github.model

import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.create.UpdateMessageInteractionResponseCreateBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    @SerialName("assets")
    val assets: List<Asset>,
    @SerialName("assets_url")
    val assetsUrl: String,
    @SerialName("author")
    val author: Author,
    @SerialName("body")
    val body: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("draft")
    val draft: Boolean,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("node_id")
    val nodeId: String,
    @SerialName("prerelease")
    val prerelease: Boolean,
    @SerialName("published_at")
    val publishedAt: String,
    @SerialName("reactions")
    val reactions: Reactions? = null,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("tarball_url")
    val tarballUrl: String,
    @SerialName("target_commitish")
    val targetCommitish: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    @SerialName("url")
    val url: String,
    @SerialName("zipball_url")
    val zipballUrl: String
)

fun GitHubRelease.toMessage(isPreview: Boolean = false): InteractionResponseModifyBuilder.() -> Unit = {
    content = name

    if (isPreview) {
        content += "\n\n⚠ Preview is not recommended if you're not willing to test for – and endure – issues. ⚠"
    }

    components = components ?: mutableListOf()
    components?.add(
        ActionRowBuilder().apply {
            linkButton(assets.first().browserDownloadUrl) {
                label = "Download"
            }
            linkButton(htmlUrl) {
                label = "Changelog"
            }
        }
    )
}

fun GitHubRelease.updateMessage(isPreview: Boolean = false): UpdateMessageInteractionResponseCreateBuilder.() -> Unit = {
    content = name

    if (isPreview) {
        content += "\n\n⚠ Preview is not recommended if you're not willing to test for – and endure – issues. ⚠"
    }

    components.add(
        ActionRowBuilder().apply {
            linkButton(assets.first().browserDownloadUrl) {
                label = "Download"
            }
            linkButton(htmlUrl) {
                label = "Changelog"
            }
        }
    )
}
