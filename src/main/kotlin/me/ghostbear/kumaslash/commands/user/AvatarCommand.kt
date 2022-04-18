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
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.UserBuilder
import me.ghostbear.kumaslash.commands.base.OnButtonInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.SlashCommand

class AvatarCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent, OnButtonInteractionCreateEvent {
    override val name: String = "avatar"
    override val description: String = "Get a user global or server avatar"
    override val parameters: MutableList<OptionsBuilder> = mutableListOf(
        UserBuilder("target", "The target user").apply {
            required = true
        }
    )

    override fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit = on@{
        val customId = interaction.component.customId ?: return@on
        if (customId.startsWith("server_avatar_")) {
            val targetId = customId.replace("server_avatar_", "")
            val user = kord.getUser(Snowflake(targetId.toLong()))?.asMember(interaction.message.getGuild().id) ?: return@on
            interaction.updatePublicMessage {
                content = user.memberAvatar?.toUrl(Image.Size.Size512)
                components = components.apply {
                    add(createActionRow(user.id))
                }
            }
        }
        if (customId.startsWith("global_avatar_")) {
            val targetId = customId.replace("global_avatar_", "")
            val user = kord.getUser(Snowflake(targetId.toLong())) ?: return@on
            interaction.updatePublicMessage {
                content = user.avatar?.toUrl(Image.Size.Size512)
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
            val memberAvatar = target.asMember(interaction.guildId).memberAvatar?.toUrl(Image.Size.Size512)
            content = memberAvatar ?: target.avatar?.toUrl(Image.Size.Size512)
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
