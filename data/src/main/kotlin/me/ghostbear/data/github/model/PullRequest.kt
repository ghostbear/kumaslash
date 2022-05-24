package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequest(
    @SerialName("url")
    val url: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("diff_url")
    val diffUrl: String = "",
    @SerialName("patch_url")
    val patchUrl: String = "",
    @SerialName("merged_at")
    val mergedAt: String? = null
)