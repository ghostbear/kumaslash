package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pull(
    @SerialName("url")
    val url: String = "",
    @SerialName("id")
    val id: Long = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("diff_url")
    val diffUrl: String = "",
    @SerialName("patch_url")
    val patchUrl: String = "",
    @SerialName("issue_url")
    val issueUrl: String = "",
    @SerialName("number")
    val number: Int = 0,
    @SerialName("state")
    val state: String = "",
    @SerialName("locked")
    val locked: Boolean = false,
    @SerialName("title")
    val title: String = "",
    @SerialName("user")
    val user: User = User(),
    @SerialName("body")
    val body: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("closed_at")
    val closedAt: String = "",
    @SerialName("merged_at")
    val mergedAt: String = "",
    @SerialName("merge_commit_sha")
    val mergeCommitSha: String = "",
    @SerialName("assignee")
    val assignee: Assignee = Assignee(),
    @SerialName("assignees")
    val assignees: List<Assignee> = listOf(),
    @SerialName("requested_reviewers")
    val requestedReviewers: List<RequestedReviewer> = listOf(),
    @SerialName("requested_teams")
    val requestedTeams: List<RequestedTeam> = listOf(),
    @SerialName("labels")
    val labels: List<Label> = listOf(),
    @SerialName("milestone")
    val milestone: Milestone = Milestone(),
    @SerialName("draft")
    val draft: Boolean = false,
    @SerialName("commits_url")
    val commitsUrl: String = "",
    @SerialName("review_comments_url")
    val reviewCommentsUrl: String = "",
    @SerialName("review_comment_url")
    val reviewCommentUrl: String = "",
    @SerialName("comments_url")
    val commentsUrl: String = "",
    @SerialName("statuses_url")
    val statusesUrl: String = "",
    @SerialName("head")
    val head: Head = Head(),
    @SerialName("base")
    val base: Base = Base(),
    @SerialName("_links")
    val links: Links = Links(),
    @SerialName("author_association")
    val authorAssociation: String = "",
    @SerialName("auto_merge")
    val autoMerge: Boolean? = null,
    @SerialName("active_lock_reason")
    val activeLockReason: String = "",
    @SerialName("merged")
    val merged: Boolean = false,
    @SerialName("mergeable")
    val mergeable: Boolean = false,
    @SerialName("rebaseable")
    val rebaseable: Boolean = false,
    @SerialName("mergeable_state")
    val mergeableState: String = "",
    @SerialName("merged_by")
    val mergedBy: MergedBy = MergedBy(),
    @SerialName("comments")
    val comments: Int = 0,
    @SerialName("review_comments")
    val reviewComments: Int = 0,
    @SerialName("maintainer_can_modify")
    val maintainerCanModify: Boolean = false,
    @SerialName("commits")
    val commits: Int = 0,
    @SerialName("additions")
    val additions: Int = 0,
    @SerialName("deletions")
    val deletions: Int = 0,
    @SerialName("changed_files")
    val changedFiles: Int = 0
)