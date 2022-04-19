package me.ghostbear.kumaslash.commands.steps

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.modify.embed
import me.ghostbear.kumaslash.commands.base.*

class StepsCommand : SlashCommand(), OnGuildChatInputCommandInteractionCreateEvent, OnModalSubmitInteractionCreateEvent {
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

class RequestStepsCommand : MessageCommand(), OnGuildMessageCommandInteractionCreateEvent, OnModalSubmitInteractionCreateEvent, OnButtonInteractionCreateEvent {
    override val name: String = "Request Steps"
    override val description: String = "Request a user to fill out the /Steps form."

    override fun onGuildMessageCommandInteractionCreateEvent(): suspend GuildMessageCommandInteractionCreateEvent.() -> Unit = on@{
        interaction.respondPublic {
            content = "${interaction.user.mention}, please follow the Troubleshooting link below, if that doesn't solve your issues, please click the Help button."
            components.add(
                ActionRowBuilder().apply {
                    interactionButton(ButtonStyle.Primary, "command-steps-request") {
                        label = "Help"
                    }
                    linkButton("https://tachiyomi.org/help/guides/troubleshooting/") {
                        label = "Troubleshooting"
                    }
                }
            )
        }
    }

    override fun onButtonInteractionCreateEvent(): suspend ButtonInteractionCreateEvent.() -> Unit = on@{
        val customId = interaction.component.customId ?: return@on
        if (customId != "command-steps-request") return@on

        interaction.modal("Answer the following questions", "command-steps-modal") {
            actionRow {
                textInput(TextInputStyle.Short, "command-step-version", "What version of the app are you on?") {
                    allowedLength = 1..50
                    placeholder = "Example: Tachiyomi 1.13.3"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "command-step-sources", "What source are you having issues with?") {
                    allowedLength = 1..50
                    placeholder = "Example: MangaDex 1.2.158"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "command-step-device", "What device are you using?") {
                    allowedLength = 1..50
                    placeholder = "Example: Google Pixel 6"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Short, "command-step-android", "What Android version are you on?") {
                    allowedLength = 1..50
                    placeholder = "Example: Android 12L"
                    required = true
                }
            }
            actionRow {
                textInput(TextInputStyle.Paragraph, "command-step-issue", "What issue are you having?") {
                    allowedLength = 10..500
                    placeholder = "Please explain your issue here in detail and include the error if there is any"
                    required = true
                }
            }
        }
    }

    override fun onModalSubmitInteractionCreateEvent(): suspend ModalSubmitInteractionCreateEvent.() -> Unit = on@{
        if (!interaction.textInputs.keys.all { it.startsWith("command-step-") }) return@on
        val response = interaction.deferPublicResponse()
        response.respond {
            embed {
                color = Color(47, 49, 54)
                field {
                    name = "What version of the app are you on?"
                    value = "${interaction.textInputs["command-step-version"]!!.value}"
                }
                field {
                    name = "What source are you having issues with?"
                    value = "${interaction.textInputs["command-step-sources"]!!.value}"
                }
                field {
                    name = "What device are you using?"
                    value = "${interaction.textInputs["command-step-device"]!!.value}"
                }
                field {
                    name = "What Android version are you on?"
                    value = "${interaction.textInputs["command-step-android"]!!.value}"
                }
                field {
                    name = "What issue are you having?"
                    value = "${interaction.textInputs["command-step-issue"]!!.value}"
                }
                footer {
                    icon = interaction.user.avatar?.url.toString()
                    text = "Answered by ${interaction.user.username}"
                }
            }
        }
    }
}
