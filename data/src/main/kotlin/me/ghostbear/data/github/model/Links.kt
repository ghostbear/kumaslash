package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Links(
    @SerialName("self")
    val self: Self = Self(),
    @SerialName("html")
    val html: Html = Html(),
    @SerialName("issue")
    val issue: PullRequestIssue = PullRequestIssue(),
    @SerialName("comments")
    val comments: Comments = Comments(),
    @SerialName("review_comments")
    val reviewComments: ReviewComments = ReviewComments(),
    @SerialName("review_comment")
    val reviewComment: ReviewComment = ReviewComment(),
    @SerialName("commits")
    val commits: Commits = Commits(),
    @SerialName("statuses")
    val statuses: Statuses = Statuses()
)