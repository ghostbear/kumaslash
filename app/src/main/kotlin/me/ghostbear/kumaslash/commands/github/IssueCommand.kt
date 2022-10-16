package me.ghostbear.kumaslash.commands.github

import com.haroldadmin.opengraphKt.getOpenGraphTags
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.string
import java.net.URL
import kotlinx.coroutines.delay
import me.ghostbear.kumaslash.util.SubCommandInitializer
import me.ghostbear.kumaslash.util.on

internal const val ARG_REPOSITORY = "repository"
internal const val ARG_NUMBER = "number"

fun InteractionCommand.getArguments(): Pair<String, String?> {
    return strings[ARG_REPOSITORY]!! to strings[ARG_NUMBER]
}

private const val NAME: String = "issue"
private const val DESCRIPTION: String = "Get issues and pull requests from GitHub"

fun Kord.issueCommand(init: SubCommandInitializer) {
    init(NAME, DESCRIPTION) {
        string(ARG_REPOSITORY, "GitHub repository") {
            required = true
            choice("Tachiyomi", "tachiyomi")
            choice("Tachiyomi Extensions", "tachiyomi-extensions")
            choice("Tachiyomi Website", "website")
            choice("Neko", "Neko")
            choice("TachiyomiJ2K", "TachiyomiJ2K")
            choice("TachiyomiSY", "TachiyomiSY")
            choice("TachiyomiAZ", "TachiyomiAZ")
        }
        string(ARG_NUMBER, "GitHub issue or pull request ID") {
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = condition@{
            if (interaction.command is SubCommand) {
                return@condition (interaction.command as SubCommand).name == NAME
            }
            false
        }
    ) {
        val (repository, number) = interaction.command.getArguments()

        var repositoryOwner: String
        val repositoryIcon: String
        when (repository) {
            "Neko" -> {
                repositoryOwner = "CarlosEsco"
                repositoryIcon = "https://raw.githubusercontent.com/CarlosEsco/Neko/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
            }
            "TachiyomiJ2K" -> {
                repositoryOwner = "Jays2Kings"
                repositoryIcon = "https://raw.githubusercontent.com/Jays2Kings/tachiyomiJ2K/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
            }
            "TachiyomiSY" -> {
                repositoryOwner = "jobobby04"
                repositoryIcon = "https://raw.githubusercontent.com/jobobby04/TachiyomiSY/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
            }
            "TachiyomiAZ" -> {
                repositoryOwner = "az4521"
                repositoryIcon = "https://github.com/az4521/TachiyomiAZ/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
            }
            else -> {
                repositoryOwner = "tachiyomiorg"
                repositoryIcon = "https://github.com/tachiyomiorg/tachiyomi/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
            }
        }

        val response = interaction.deferPublicResponse()

        try {
            val data = GitHubApi.getIssueOrPullRequest(repositoryOwner, repository, number)
            val tags = URL("https://github.com/$repositoryOwner/$repository/pull/$number").getOpenGraphTags()
            response.respond(data.buildResponse(repository, tags, repositoryOwner, repositoryIcon))
        } catch (e: Exception) {
            e.printStackTrace()
            response.respond {
                content = "Issue or Pull Request not found"
            }
            delay(1000)
            response.delete()
        }
    }
}
