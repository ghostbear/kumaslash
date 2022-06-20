package me.ghostbear.kumaslash.commands.rules

import dev.kord.common.Color
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.int
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.create.embed
import me.ghostbear.kumaslash.util.createChatInputCommand
import me.ghostbear.kumaslash.util.on

private const val NAME: String = "rules"
private const val DESCRIPTION: String = "Send list of server rules to chat"

private const val RULE_1: String = "1. **You have to read the following channels:** <#349477891215589379> & <#738862409284059239>."
private const val RULE_2: String = "2. **You must not ask for support in any non-support channels.** Support channels includes <#349436576037732355>, <#566590778323763207> and the forks channels."
private const val RULE_3: String = "3. **You must use the English language outside of the support channels.** It's fine to use another language in the support channels but nowhere else."
private const val RULE_4: String = "4. **Use common sense, don't behave poorly.** This includes but is not limited to spamming, posting NSFW in SFW channels, treating members poorly, not listening to staff and more."
private const val RULE_5: String = "5. **Follow the Discord community guidelines.** We didn't make these rules but we will enforce them, we don't make exceptions for anyone. [Read them here!](https://discord.com/guidelines)"
private const val RULE_6: String = "6. **You must not circumvent the word filter.** It's set there for a reason."
private const val RULE_7: String = "7. **Spoilable content needs to be marked with context.** Not everyone's definition is the same, you can read ours by using the `!spoilers` tag."
private const val RULE_8: String = "8. **Do not advertise unprompted.** This includes posting server invites, advertisements, etc without permission from a staff member. This also includes DMing fellow members."
private const val RULE_9: String = "9. **No impersonation.** Especially of members with roles."
private const val RULE_10: String = "10. **No source recommendations.** Source recommendations increase the amount of load that extensions get, which causes them to ban us."
private const val RULE_11: String = "11. **Your name must be auto-fill compliant.** That means it must be possible to get Discord to suggest you just by typing `A-Z`/`0-9` characters after **\\@**."

private val RULES = listOf(RULE_1, RULE_2, RULE_3, RULE_4, RULE_5, RULE_6, RULE_7, RULE_8, RULE_9, RULE_10, RULE_11)

suspend fun Kord.rulesCommand() {
    createChatInputCommand(NAME, DESCRIPTION) {
        user("target", "Mention the rules to a specified user") {
            required = false
        }
        int("rule", "Specific rules to send") {
            required = false
        }
    }
    on<GuildChatInputCommandInteractionCreateEvent>(
        condition = {
            interaction.command.rootName == NAME
        },
    ) {
        val command = interaction.command

        val target = command.users["target"]
        val rule = command.integers["rule"]

        val member = target?.asMember(interaction.guildId)

        if (rule !== null) {
            if (rule <= RULES.size) {
                interaction.respondPublic {
                    if (member != null) {
                        content = member.mention
                    }
                    embed {
                        title = "Server rule"
                        color = Color(47, 49, 54)
                        description = RULES[Math.toIntExact(rule) - 1]
                        footer {
                            text = "Full list of rules can be found in #rules or by using /rules."
                        }
                    }
                }
            } else {
                interaction.respondEphemeral {
                    content = "Please try again with a valid rule."
                }
            }
        } else {
            interaction.respondPublic {
                if (member != null) {
                    content = member.mention
                }
                embed {
                    title = "Server rules"
                    color = Color(47, 49, 54)
                    description = RULES.joinToString("\n\n")
                }
            }
        }
    }
}