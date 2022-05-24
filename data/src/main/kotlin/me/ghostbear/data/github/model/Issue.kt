package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Issue(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("repository_url")
    val repositoryUrl: String = "",
    @SerialName("labels_url")
    val labelsUrl: String = "",
    @SerialName("comments_url")
    val commentsUrl: String = "",
    @SerialName("events_url")
    val eventsUrl: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("number")
    val number: Int = 0,
    @SerialName("state")
    val state: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("body")
    val body: String = "",
    @SerialName("user")
    val user: User = User(),
    @SerialName("labels")
    val labels: List<Label> = listOf(),
    @SerialName("assignee")
    val assignee: Assignee? = null,
    @SerialName("assignees")
    val assignees: List<Assignee> = listOf(),
    @SerialName("milestone")
    val milestone: Milestone? = null,
    @SerialName("locked")
    val locked: Boolean = false,
    @SerialName("active_lock_reason")
    val activeLockReason: String? = null,
    @SerialName("comments")
    val comments: Int = 0,
    @SerialName("pull_request")
    val pullRequest: PullRequest? = null,
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("closed_by")
    val closedBy: ClosedBy? = null,
    @SerialName("author_association")
    val authorAssociation: String = "",
    @SerialName("draft")
    val draft: Boolean = false,
    @SerialName("reactions")
    val reactions: Reactions = Reactions(),
    @SerialName("timeline_url")
    val timelineUrl: String = ""
)