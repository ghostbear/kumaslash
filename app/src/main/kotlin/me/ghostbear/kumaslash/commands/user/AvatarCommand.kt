package me.ghostbear.kumaslash.commands.user

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.updatePublicMessage
import dev.kord.core.entity.Icon
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
import me.ghostbear.kumaslash.util.SubCommandInitializer
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "avatar"
private const val DESCRIPTION: String = "Get a user global or server avatar"

fun Kord.avatarCommand(init: SubCommandInitializer) {
    init(NAME, DESCRIPTION) {
        user("target", "The target user") {
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
        val command = interaction.command

        val target = command.users["target"]!!

        interaction.respondPublic {
            val member = target.asMember(interaction.guildId)
            val memberAvatar = member.memberAvatar?.toUrl(Image.Size.Size512)
            val targetAvatar = target.avatar?.toUrl(Image.Size.Size512)
            embed {
                image = memberAvatar ?: targetAvatar
                footer {
                    text = "Avatar for ${member.username}"
                }
            }
            if (memberAvatar != null) {
                actionRow(createActionRow(target.id))
            }
        }
    }
    on<ButtonInteractionCreateEvent>(
        condition = condition@{
            val customId = interaction.component.customId ?: return@condition false
            listOf("server_avatar_", "global_avatar_").any { customId.startsWith(it) }
        }
    ) {
        val customId = interaction.component.customId ?: return@on
        if (customId.startsWith("server_avatar_")) {
            val targetId = customId.replace("server_avatar_", "")
            val user =
                kord.getUser(Snowflake(targetId.toLong()))?.asMember(interaction.message.getGuild().id) ?: return@on
            interaction.updatePublicMessage {
                embed {
                    image = user.memberAvatar?.toUrl(Image.Size.Size512)
                    footer {
                        text = "Avatar for ${user.username}"
                    }
                }
                actionRow(createActionRow(user.id))
            }
        }
        if (customId.startsWith("global_avatar_")) {
            val targetId = customId.replace("global_avatar_", "")
            val user = kord.getUser(Snowflake(targetId.toLong())) ?: return@on
            interaction.updatePublicMessage {
                embed {
                    image = user.avatar?.toUrl(Image.Size.Size512)
                    footer {
                        text = "Avatar for ${user.username}"
                    }
                }
                actionRow(createActionRow(user.id, AvatarType.GLOBAL))
            }
        }
    }
}

fun Icon.toUrl(imageSize: Image.Size): String =
    cdnUrl.toUrl {
        size = imageSize
        format = this@toUrl.format
    }

private enum class AvatarType {
    SERVER,
    GLOBAL
}

private fun createActionRow(userId: Snowflake, type: AvatarType = AvatarType.SERVER): ActionRowBuilder.() -> Unit = {
    interactionButton(ButtonStyle.Primary, "server_avatar_$userId") {
        label = "Server Avatar"
        disabled = type == AvatarType.SERVER
    }
    interactionButton(ButtonStyle.Primary, "global_avatar_$userId") {
        label = "Global Avatar"
        disabled = type == AvatarType.GLOBAL
    }
}
