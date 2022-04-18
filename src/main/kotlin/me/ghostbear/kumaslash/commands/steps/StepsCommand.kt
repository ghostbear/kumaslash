package me.ghostbear.kumaslash.commands.steps

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.message.modify.embed
import me.ghostbear.kumaslash.commands.base.OnGuildChatInputCommandInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.OnModalSubmitInteractionCreateEvent
import me.ghostbear.kumaslash.commands.base.SlashCommand

class StepsCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent, OnModalSubmitInteractionCreateEvent {
    override val name: String = "steps"
    override val description: String = "Answer these questions to receive better support."

    override fun onGuildChatInputCommandInteractionCreateEvent(): suspend GuildChatInputCommandInteractionCreateEvent.() -> Unit = on@{
        try {
            interaction.modal("Answer the following questions", "steps-modal") {
                actionRow {
                    textInput(TextInputStyle.Short, "step-version", "What version of the app are you on?") {
                        allowedLength = 1..50
                        placeholder = "Example: Tachiyomi 1.13.3"
                        required = true
                    }
                }
                actionRow {
                    textInput(TextInputStyle.Short, "step-sources", "What source are you having issues with?") {
                        allowedLength = 1..50
                        placeholder = "Example: MangaDex 1.2.158"
                        required = true
                    }
                }
                actionRow {
                    textInput(TextInputStyle.Short, "step-device", "What device are you using?") {
                        allowedLength = 1..50
                        placeholder = "Example: Google Pixel 6"
                        required = true
                    }
                }
                actionRow {
                    textInput(TextInputStyle.Short, "step-android", "What Android version are you on?") {
                        allowedLength = 1..50
                        placeholder = "Example: Android 12L"
                        required = true
                    }
                }
                actionRow {
                    textInput(TextInputStyle.Paragraph, "step-issue", "What issue are you having?") {
                        allowedLength = 10..500
                        placeholder = "Please explain your issue here in detail and include the error if there is any"
                        required = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onModalSubmitInteractionCreateEvent(): suspend ModalSubmitInteractionCreateEvent.() -> Unit = on@{
        try {
            if (!interaction.textInputs.keys.all { it.startsWith("step-") }) return@on
            val response = interaction.deferPublicResponse()
            response.respond {
                embed {
                    color = interaction.user.accentColor
                    field {
                        name = "What version of the app are you on?"
                        value = "```${interaction.textInputs["step-version"]!!.value}```"
                    }
                    field {
                        name = "What source are you having issues with?"
                        value = "```${interaction.textInputs["step-sources"]!!.value}```"
                    }
                    field {
                        name = "What device are you using?"
                        value = "```${interaction.textInputs["step-device"]!!.value}```"
                    }
                    field {
                        name = "What Android version are you on?"
                        value = "```${interaction.textInputs["step-android"]!!.value}```"
                    }
                    field {
                        name = "What issue are you having?"
                        value = "```${interaction.textInputs["step-issue"]!!.value}```"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
