package me.ghostbear.kumaslash.commands.steps

import dev.kord.common.Color
import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.message.create.embed
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "steps"
private const val DESCRIPTION: String = "Answer these questions to receive better support."

suspend fun Kord.stepsCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        defaultMemberPermissions = Permissions(DiscordBitSet(0))
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        }
    ) {
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
    on<ModalSubmitInteractionCreateEvent>(
        condition = {
            interaction.textInputs.keys.all { it.startsWith("slash-step-") }
        }
    ) {
        interaction.respondPublic {
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
