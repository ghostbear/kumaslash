package me.ghostbear.web.extension.remote

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

