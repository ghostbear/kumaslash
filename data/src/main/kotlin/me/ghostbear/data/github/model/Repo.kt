package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    @SerialName("owner")
    val owner: Owner = Owner(),
    @SerialName("private")
    val `private`: Boolean = false,
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("fork")
    val fork: Boolean = false,
    @SerialName("url")
    val url: String = "",
    @SerialName("archive_url")
    val archiveUrl: String = "",
    @SerialName("assignees_url")
    val assigneesUrl: String = "",
    @SerialName("blobs_url")
    val blobsUrl: String = "",
    @SerialName("branches_url")
    val branchesUrl: String = "",
    @SerialName("collaborators_url")
    val collaboratorsUrl: String = "",
    @SerialName("comments_url")
    val commentsUrl: String = "",
    @SerialName("commits_url")
    val commitsUrl: String = "",
    @SerialName("compare_url")
    val compareUrl: String = "",
    @SerialName("contents_url")
    val contentsUrl: String = "",
    @SerialName("contributors_url")
    val contributorsUrl: String = "",
    @SerialName("deployments_url")
    val deploymentsUrl: String = "",
    @SerialName("downloads_url")
    val downloadsUrl: String = "",
    @SerialName("events_url")
    val eventsUrl: String = "",
    @SerialName("forks_url")
    val forksUrl: String = "",
    @SerialName("git_commits_url")
    val gitCommitsUrl: String = "",
    @SerialName("git_refs_url")
    val gitRefsUrl: String = "",
    @SerialName("git_tags_url")
    val gitTagsUrl: String = "",
    @SerialName("git_url")
    val gitUrl: String = "",
    @SerialName("issue_comment_url")
    val issueCommentUrl: String = "",
    @SerialName("issue_events_url")
    val issueEventsUrl: String = "",
    @SerialName("issues_url")
    val issuesUrl: String = "",
    @SerialName("keys_url")
    val keysUrl: String = "",
    @SerialName("labels_url")
    val labelsUrl: String = "",
    @SerialName("languages_url")
    val languagesUrl: String = "",
    @SerialName("merges_url")
    val mergesUrl: String = "",
    @SerialName("milestones_url")
    val milestonesUrl: String = "",
    @SerialName("notifications_url")
    val notificationsUrl: String = "",
    @SerialName("pulls_url")
    val pullsUrl: String = "",
    @SerialName("releases_url")
    val releasesUrl: String = "",
    @SerialName("ssh_url")
    val sshUrl: String = "",
    @SerialName("stargazers_url")
    val stargazersUrl: String = "",
    @SerialName("statuses_url")
    val statusesUrl: String = "",
    @SerialName("subscribers_url")
    val subscribersUrl: String = "",
    @SerialName("subscription_url")
    val subscriptionUrl: String = "",
    @SerialName("tags_url")
    val tagsUrl: String = "",
    @SerialName("teams_url")
    val teamsUrl: String = "",
    @SerialName("trees_url")
    val treesUrl: String = "",
    @SerialName("clone_url")
    val cloneUrl: String = "",
    @SerialName("mirror_url")
    val mirrorUrl: String = "",
    @SerialName("hooks_url")
    val hooksUrl: String = "",
    @SerialName("svn_url")
    val svnUrl: String = "",
    @SerialName("homepage")
    val homepage: String = "",
    @SerialName("language")
    val language: String? = null,
    @SerialName("forks_count")
    val forksCount: Int = 0,
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    @SerialName("watchers_count")
    val watchersCount: Int = 0,
    @SerialName("size")
    val size: Int = 0,
    @SerialName("default_branch")
    val defaultBranch: String = "",
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0,
    @SerialName("topics")
    val topics: List<String> = listOf(),
    @SerialName("has_issues")
    val hasIssues: Boolean = false,
    @SerialName("has_projects")
    val hasProjects: Boolean = false,
    @SerialName("has_wiki")
    val hasWiki: Boolean = false,
    @SerialName("has_pages")
    val hasPages: Boolean = false,
    @SerialName("has_downloads")
    val hasDownloads: Boolean = false,
    @SerialName("archived")
    val archived: Boolean = false,
    @SerialName("disabled")
    val disabled: Boolean = false,
    @SerialName("pushed_at")
    val pushedAt: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("permissions")
    val permissions: Permissions = Permissions(),
    @SerialName("allow_rebase_merge")
    val allowRebaseMerge: Boolean = false,
    @SerialName("temp_clone_token")
    val tempCloneToken: String = "",
    @SerialName("allow_squash_merge")
    val allowSquashMerge: Boolean = false,
    @SerialName("allow_merge_commit")
    val allowMergeCommit: Boolean = false,
    @SerialName("allow_forking")
    val allowForking: Boolean = false,
    @SerialName("forks")
    val forks: Int = 0,
    @SerialName("open_issues")
    val openIssues: Int = 0,
    @SerialName("license")
    val license: License = License(),
    @SerialName("watchers")
    val watchers: Int = 0
)