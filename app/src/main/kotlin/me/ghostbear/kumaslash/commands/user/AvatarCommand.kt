package me.ghostbear.kumaslash.commands.user

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.updatePublicMessage
import dev.kord.core.entity.Icon
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import me.ghostbear.core.OnButtonInteractionCreateEvent
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig

class AvatarCommand : SubSlashCommand(), OnGuildChatInputCommandInteractionCreateEvent, OnButtonInteractionCreateEvent {
    override val name: String = "avatar"
    override val description: String = "Get a user global or server avatar"
    override val config: SubSlashCommandConfig = {
        user("target", "The target user") {
            required = true
        }
    }

    override fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit = on@{
        val customId = interaction.component.customId ?: return@on
        if (customId.startsWith("server_avatar_")) {
            val targetId = customId.replace("server_avatar_", "")
            val user = kord.getUser(Snowflake(targetId.toLong()))?.asMember(interaction.message.getGuild().id) ?: return@on
            interaction.updatePublicMessage {
                embed {
                    image = user.memberAvatar?.toUrl(Image.Size.Size512)
                    footer {
                        text = "Avatar for ${user.username}"
                    }
                }
                components = components.apply {
                    add(createActionRow(user.id))
                }
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
                components = components.apply {
                    add(createActionRow(user.id, AvatarType.GLOBAL))
                }
            }
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        val command = interaction.command
        val response = interaction.deferPublicResponse()

        val target = command.users["target"]!!

        response.respond {
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
                components = (components ?: mutableListOf()).apply {
                    add(createActionRow(target.id))
                }
            }
        }
    }
}

fun Icon.toUrl(imageSize: Image.Size): String = cdnUrl.toUrl {
    size = imageSize
    format = this@toUrl.format
}

private enum class AvatarType {
    SERVER,
    GLOBAL
}

private fun createActionRow(userId: Snowflake, type: AvatarType = AvatarType.SERVER): ActionRowBuilder {
    return ActionRowBuilder().apply {
        interactionButton(ButtonStyle.Primary, "server_avatar_$userId") {
            label = "Server Avatar"
            disabled = type == AvatarType.SERVER
        }
        interactionButton(ButtonStyle.Primary, "global_avatar_$userId") {
            label = "Global Avatar"
            disabled = type == AvatarType.GLOBAL
        }
    }
}
