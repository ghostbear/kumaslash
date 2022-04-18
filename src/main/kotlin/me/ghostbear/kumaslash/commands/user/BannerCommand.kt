package me.ghostbear.kumaslash.commands.user

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.UserBuilder
import me.ghostbear.kumaslash.commands.base.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.SlashCommand

class BannerCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "banner"
    override val description: String = "Get a user global or server banner"
    override val parameters: MutableList<OptionsBuilder> = mutableListOf(
        UserBuilder("target", "The target user").apply {
            required = true
        }
    )

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = {
        val command = interaction.command
        val response = interaction.deferPublicResponse()

        val target = command.users["target"]!!

        val userData = kord.rest.user.getUser(target.id).toData()
        val user = User(
            userData,
            kord
        )

        response.respond {
            content = user.bannerUrl?.let { "$it?size=4096" } ?: "Not found"
        }
    }
}

val User.bannerUrl: String?
    get() {
        val banner = data.banner ?: return null
        return if (banner.startsWith("a_")) {
            getBannerUrl(Image.Format.GIF)
        } else {
            getBannerUrl(Image.Format.PNG)
        }
    }
