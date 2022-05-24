package me.ghostbear.data.tachiyomi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("lang")
    val language: String = "",
    @SerialName("baseUrl")
    val baseUrl: String = "",
    @SerialName("versionId")
    val versionId: Int = 0,
    @SerialName("hasCloudflare")
    val hasCloudflare: Int = 0
)
