package me.ghostbear.kumaslash.commands.steps

import dev.kord.common.Color
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.message.modify.embed
import me.ghostbear.core.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.core.OnModalSubmitInteractionCreateEvent
import me.ghostbear.core.SlashCommand

class StepsCommand :
    SlashCommand(),
    OnGuildChatInputCommandInteractionCreateEvent,
    OnModalSubmitInteractionCreateEvent {
    override val name: String = "steps"
    override val description: String = "Answer these questions to receive better support."

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        interaction.modal("Answer the following questions", "slash-steps-modal") {
            actionRow {
                textInput(TextInputStyle.Short, "slash-step-version", "What version of the app are you on?") {
                    allowedLength = 1..50
                    placeholder = "Example: Tachiyomi 1.13.3"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "slash-step-sources", "What source are you having issues with?") {
                    allowedLength = 1..50
                    placeholder = "Example: MangaDex 1.2.158"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "slash-step-device", "What device are you using?") {
                    allowedLength = 1..50
                    placeholder = "Example: Google Pixel 6"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "slash-step-android", "What Android version are you on?") {
                    allowedLength = 1..50
                    placeholder = "Example: Android 12L"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Paragraph, "slash-step-issue", "What issue are you having?") {
                    allowedLength = 10..500
                    placeholder = "Please explain your issue here in detail and include the error if there is any"
                    required = true
                }
            }
        }
    }

    override fun onModalSubmitInteractionCreateEvent(): suspend ModalSubmitInteractionCreateEvent.() -> Unit = on@{
        if (!interaction.textInputs.keys.all { it.startsWith("slash-step-") }) return@on
        val response = interaction.deferPublicResponse()
        response.respond {
            embed {
                color = Color(47, 49, 54)
                field {
                    name = "What version of the app are you on?"
                    value = "${interaction.textInputs["slash-step-version"]!!.value}"
                }
                field {
                    name = "What source are you having issues with?"
                    value = "${interaction.textInputs["slash-step-sources"]!!.value}"
                }
                field {
                    name = "What device are you using?"
                    value = "${interaction.textInputs["slash-step-device"]!!.value}"
                }
                field {
                    name = "What Android version are you on?"
                    value = "${interaction.textInputs["slash-step-android"]!!.value}"
                }
                field {
                    name = "What issue are you having?"
                    value = "${interaction.textInputs["slash-step-issue"]!!.value}"
                }
                footer {
                    icon = interaction.user.avatar?.url.toString()
                    text = "Answered by ${interaction.user.username}"
                }
            }
        }
    }
}
