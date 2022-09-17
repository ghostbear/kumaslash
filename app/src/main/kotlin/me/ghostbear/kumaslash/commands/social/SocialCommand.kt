package me.ghostbear.kumaslash.commands.social

import dev.kord.common.Color
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.int
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.create.embed
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "social"
private const val DESCRIPTION: String = "Use Social commands with another user"

suspend fun Kord.socialCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        int("activity", "What activity do you want to do?") {
            choice("Bite", 0)
            choice("Hug", 1)
            choice("Kiss", 2)
            choice("Lick", 3)
            choice("Nuzzle", 4)
            choice("Slap", 5)
            choice("Headpat", 6)
            required = true
        }
        user("target", "Who do you want to interact with?") {
            required = true
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        },
    ) {
        val command = interaction.command

        val target = command.users["target"]
        val activity = command.integers["activity"]

        val userSelf = interaction.user.asMember(interaction.guildId)
        val userTarget = target?.asMember(interaction.guildId)

        if (userTarget?.id != userSelf.id) {
            interaction.respondPublic {
                content = userTarget?.mention
                embed {
                    description = listOf(
                        "**${userSelf.displayName}** bites **${userTarget?.displayName}** 🫦",
                        "**${userSelf.displayName}** hugs **${userTarget?.displayName}** 🫂",
                        "**${userSelf.displayName}** kisses **${userTarget?.displayName}** 💞",
                        "**${userSelf.displayName}** licks **${userTarget?.displayName}** 👅",
                        "**${userSelf.displayName}** nuzzles **${userTarget?.displayName}** uWu",
                        "**${userSelf.displayName}** slaps **${userTarget?.displayName}** to high heavens 🤚",
                        "**${userSelf.displayName}** headpats **${userTarget?.displayName}** :3",
                    )[activity!!.toInt()]
                    // Goal here is to make the command eventually be able to add/remove/get images, like the logging command, instead of having to PR the bot each time.
                    image = listOf(
                        SocialImages.SOCIAL_IMAGE_BITE,
                        SocialImages.SOCIAL_IMAGE_HUG,
                        SocialImages.SOCIAL_IMAGE_KISS,
                        SocialImages.SOCIAL_IMAGE_LICK,
                        SocialImages.SOCIAL_IMAGE_NUZZLE,
                        SocialImages.SOCIAL_IMAGE_SLAP,
                        SocialImages.SOCIAL_IMAGE_HEADPAT
                    )[activity!!.toInt()].random().toString()
                    color = Color(255, 130, 172)
                }
            }
        } else {
            interaction.respondEphemeral {
                content = "You need to mention someone other than yourself to do this!"
            }
        }
    }
}