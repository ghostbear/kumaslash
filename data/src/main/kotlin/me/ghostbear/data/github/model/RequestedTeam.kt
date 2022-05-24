package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestedTeam(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("privacy")
    val privacy: String = "",
    @SerialName("permission")
    val permission: String = "",
    @SerialName("members_url")
    val membersUrl: String = "",
    @SerialName("repositories_url")
    val repositoriesUrl: String = ""
)