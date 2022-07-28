package me.ghostbear.kumaslash.commands.user

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.modify.embed
import kotlinx.coroutines.delay
import me.ghostbear.kumaslash.util.SubCommandInitializer
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "banner"
private const val DESCRIPTION: String = "Get a user global or server banner"
fun Kord.bannerCommand(init: SubCommandInitializer) {
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
        val response = interaction.deferPublicResponse()

        val target = interaction.command.users["target"]!!

        val userData = kord.rest.user.getUser(target.id).toData()
        val user = User(
            userData,
            kord
        )

        val bannerUrl = user.bannerUrl
        if (bannerUrl != null) {
            response.respond {
                embed {
                    color = Color(47, 49, 54)
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
