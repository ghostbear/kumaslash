package me.ghostbear.kumaslash.commands.user

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.modify.embed
import kotlinx.coroutines.delay
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.SubSlashCommand
import me.ghostbear.core.SubSlashCommandConfig

class BannerCommand : SubSlashCommand(), OnGuildChatInputCommandInteractionCreateEvent {
    override val name: String = "banner"
    override val description: String = "Get a user global or server banner"
    override val config: SubSlashCommandConfig = {
        user("target", "The target user") {
            required = true
        }
    }

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = {
        val command = interaction.command
        val response = interaction.deferPublicResponse()

        val target = command.users["target"]!!

        val userData = kord.rest.user.getUser(target.id).toData()
        val user = User(
            userData,
            kord
        )

        val bannerUrl = user.bannerUrl
        if (bannerUrl != null) {
            response.respond {
                embed {
                    image = "$bannerUrl?size=4096"
                    footer {
                        text = "Banner for ${user.username}"
                    }
                }
            }
        } else {
            response.respond {
                content = "Not found"
            }
            delay(1000)
            response.delete()
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
