package me.ghostbear.kumaslash.commands.feature

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

private const val NAME: String = "feature"
private const val DESCRIPTION: String = "Request an app or source feature"

internal const val ARG_FEATURE_TYPE = "type"
internal const val ARG_APPLICATION = "application"

fun InteractionCommand.getArguments(): Pair<String, String?> {
    return strings[ARG_FEATURE_TYPE]!! to strings[ARG_APPLICATION]
}

suspend fun Kord.featureCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        string(ARG_FEATURE_TYPE,"What type of feature do you want to request?") {
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
        val (featureType, application) = interaction.command.getArguments()

        var repositoryUser = ""
        var repositoryType = ""
        var applicationName = ""
        var applicationIcon = ""
        var issueReportType = ""

        when (featureType) {
            "app" -> {
                when (application) {
                    "tachiyomi" -> {
                        repositoryUser = "tachiyomiorg"
                        repositoryType = "tachiyomi"
                        applicationName = "Tachiyomi"
                        issueReportType = "issues?q=is%3Aissue+label%3A\"Feature+request\""
                        applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "neko" -> {
                        repositoryUser = "CarlosEsco"
                        repositoryType = "Neko"
                        issueReportType = "issues?q=is%3Aissue+label%3Aenhancement"
                        applicationName = "Neko"
                        applicationIcon = "https://raw.githubusercontent.com/CarlosEsco/Neko/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-j2k" -> {
                        repositoryUser = "Jays2Kings"
                        repositoryType = "tachiyomiJ2K"
                        issueReportType = "issues?q=is%3Aissue+label%3Afeature"
                        applicationName = "TachiyomiJ2K"
                        applicationIcon = "https://raw.githubusercontent.com/Jays2Kings/tachiyomiJ2K/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-sy" -> {
                        repositoryUser = "jobobby04"
                        repositoryType = "TachiyomiSY"
                        issueReportType = "/issues?q=is%3Aissue+label%3A\"Feature+request\""
                        applicationName = "TachiyomiSY"
                        applicationIcon = "https://raw.githubusercontent.com/jobobby04/TachiyomiSY/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                    "tachiyomi-az" -> {
                        repositoryUser = "az4521"
                        repositoryType = "tachiyomiAZ"
                        issueReportType = "issues?q=is%3Aissue+label%3Aenhancement"
                        applicationName = "TachiyomiAZ"
                        applicationIcon = "https://raw.githubusercontent.com/az4521/TachiyomiAZ/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
                    }
                }
            }
            "source" -> {
                repositoryUser = "tachiyomiorg"
                repositoryType = "tachiyomi-extensions"
                issueReportType = "issues?q=is%3Aissue+label%3A\"Feature+request\""
                applicationName = "Tachiyomi Extensions"
                applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
            }
            "website" -> {
                repositoryUser = "tachiyomiorg"
                repositoryType = "website"
                issueReportType = "issues"
                applicationName = "Tachiyomi Website"
                applicationIcon = "https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
            }
        }

        val response = interaction.deferPublicResponse()

        response.respond {
            when (featureType) {
                "source" -> {
                    embed {
                        color = Color(47, 49, 54)
                        thumbnail {
                            url = applicationIcon
                        }
                        title = "Request source or feature for $applicationName"
                        description = "To request a source, or a source feature, click the link below.\n\nMake sure that you're not requesting something that has already been requested."
                    }
                    components = mutableListOf(
                        ActionRowBuilder().apply {
                            linkButton("https://github.com/$repositoryUser/$repositoryType/$issueReportType") {
                                label = "Search for requested features"
                            }
                            linkButton("https://github.com/$repositoryUser/$repositoryType/$issueReportType") {
                                label = "Search for requested sources"
                            }
                        }
                    )
                }
                else -> {
                    embed {
                        color = Color(47, 49, 54)
                        thumbnail {
                            url = applicationIcon
                        }
                        title = "Request $featureType feature for $applicationName"
                        description = "To request a feature, click the link below.\n\nMake sure that you're not requesting something that has already been requested."
                    }
                    components = mutableListOf(
                        ActionRowBuilder().apply {
                            linkButton("https://github.com/$repositoryUser/$repositoryType/$issueReportType") {
                                label = "Search for requested features"
                            }
                        }
                    )
                }
            }
        }
    }
}