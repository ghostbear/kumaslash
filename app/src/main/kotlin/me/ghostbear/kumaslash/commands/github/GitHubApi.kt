package me.ghostbear.kumaslash.commands.github

import me.ghostbear.data.github.model.Issue as GithubModelIssue
import me.ghostbear.data.github.model.Pull as GithubModelPull
import com.haroldadmin.opengraphKt.Tags
import dev.kord.common.Color
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import dev.kord.rest.builder.message.modify.embed
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import me.ghostbear.kumaslash.client

sealed class GitHubResponse

data class Pull(val pull: GithubModelPull) : GitHubResponse()

data class Issue(val issue: GithubModelIssue) : GitHubResponse()

object GitHubApi {

    suspend fun getIssueOrPullRequest(owner: String, repository: String, number: String?): GitHubResponse {
        val head = client.request("https://api.github.com/repos/$owner/$repository/pulls/$number") {
            method = HttpMethod.Head
        }

        return if (head.status != HttpStatusCode.NotFound) {
            val data = client.get("https://api.github.com/repos/$owner/$repository/pulls/$number").body<GithubModelPull>()
            Pull(data)
        } else {
            val data = client.get("https://api.github.com/repos/$owner/$repository/issues/$number").body<GithubModelIssue>()
            Issue(data)
        }
    }
}

fun GitHubResponse.buildResponse(repository: String, tags: Tags): InteractionResponseModifyBuilder.() -> Unit {
    return when (this) {
        is Issue -> {
            {
                embed {
                    color = Color(47, 49, 54)
                    image = tags.image
                    url = issue.htmlUrl
                    title = issue.title
                    description = issue.body
                    author {
                        name = "$repository · #${issue.number}"
                    }
                    footer {
                        icon = issue.user.avatarUrl
                        text = "Issue by ${issue.user.login}"
                    }
                }
                components = mutableListOf(
                    ActionRowBuilder().apply {
                        linkButton(issue.htmlUrl) {
                            label = "Open issue in browser"
                        }
                    }
                )
            }
        }
        is Pull -> {
            {
                embed {
                    color = Color(47, 49, 54)
                    image = tags.image
                    url = pull.htmlUrl
                    title = pull.title
                    description = pull.body
                    author {
                        name = "$repository · #${pull.number}"
                    }
                    footer {
                        icon = pull.user.avatarUrl
                        text = "Pull Request by ${pull.user.login}"
                    }
                }
                components = mutableListOf(
                    ActionRowBuilder().apply {
                        linkButton(pull.htmlUrl) {
                            label = "Open pull Request in browser"
                        }
                    }
                )
            }
        }
    }
}