package me.ghostbear.kumaslash.commands.bug

import dev.kord.common.Color
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.embed
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "bug"
private const val DESCRIPTION: String = "Report an app or source bug"

internal const val ARG_BUG_TYPE = "type"
internal const val ARG_APPLICATION = "application"

fun InteractionCommand.getArguments(): Pair<String, String?> {
    return strings[ARG_BUG_TYPE]!! to strings[ARG_APPLICATION]
}

suspend fun Kord.bugCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        string(ARG_BUG_TYPE,"What type of Bug are you experiencing?") {
            choice("App", "app")
            choice("Source", "source")
            choice("Website", "website")
            required = true
        }
        string(ARG_APPLICATION,"With what App?") {
            choice("Tachiyomi", "tachiyomi")
            choice("Neko", "neko")
            choice("Tachiyomi J2K", "tachiyomi-j2k")
            choice("Tachiyomi SY", "tachiyomi-sy")
            choice("Tachiyomi AZ", "tachiyomi-az")
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        },
    ) {
        val (bugType, application) = interaction.command.getArguments()

        var repositoryUser = ""
        var repositoryType = ""
        var applicationName = ""
        var applicationIcon = ""
        var issueReportType = "issues?q=is%3Aissue+label%3ABug"

        when (bugType) {
            "app" -> {
                when (application) {
                    "tachiyomi" -> {
                        repositoryUser = "tachiyomiorg"
                        repositoryType = "tachiyomi"
                        applicationName = "Tachiyomi"
                        applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "neko" -> {
                        repositoryUser = "CarlosEsco"
                        repositoryType = "Neko"
                        applicationName = "Neko"
                        applicationIcon = "https://raw.githubusercontent.com/CarlosEsco/Neko/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-j2k" -> {
                        repositoryUser = "Jays2Kings"
                        repositoryType = "tachiyomiJ2K"
                        applicationName = "TachiyomiJ2K"
                        applicationIcon = "https://raw.githubusercontent.com/Jays2Kings/tachiyomiJ2K/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-sy" -> {
                        repositoryUser = "jobobby04"
                        repositoryType = "TachiyomiSY"
                        applicationName = "TachiyomiSY"
                        applicationIcon = "https://raw.githubusercontent.com/jobobby04/TachiyomiSY/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-az" -> {
                        repositoryUser = "az4521"
                        repositoryType = "tachiyomiAZ"
                        applicationName = "TachiyomiAZ"
                        applicationIcon = "https://raw.githubusercontent.com/az4521/TachiyomiAZ/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                }
            }
            "source" -> {
                repositoryUser = "tachiyomiorg"
                repositoryType = "tachiyomi-extensions"
                applicationName = "Tachiyomi Extensions"
                applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
            }
            "website" -> {
                repositoryUser = "tachiyomiorg"
                repositoryType = "website"
                applicationName = "Tachiyomi Website"
                applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
            }
        }

        val response = interaction.deferPublicResponse()

        response.respond {
            embed {
                color = Color(47, 49, 54)
                title = "Report $bugType issue for $applicationName"
                description = "To report a bug/issue, click the link below.\n\nMake sure that you're not reporting something that has already been reported."
                thumbnail {
                    url = applicationIcon
                }
            }
            components = mutableListOf(
                ActionRowBuilder().apply {
                    linkButton("https://github.com/$repositoryUser/$repositoryType/$issueReportType") {
                        label = "Search for reported bugs"
                    }
                }
            )
        }
    }
}