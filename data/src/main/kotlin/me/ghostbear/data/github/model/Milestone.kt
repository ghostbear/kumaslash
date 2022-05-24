package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Milestone(
    @SerialName("url")
    val url: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("labels_url")
    val labelsUrl: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("number")
    val number: Int = 0,
    @SerialName("state")
    val state: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("creator")
    val creator: Creator = Creator(),
    @SerialName("open_issues")
    val openIssues: Int = 0,
    @SerialName("closed_issues")
    val closedIssues: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("closed_at")
    val closedAt: String = "",
    @SerialName("due_on")
    val dueOn: String = ""
)