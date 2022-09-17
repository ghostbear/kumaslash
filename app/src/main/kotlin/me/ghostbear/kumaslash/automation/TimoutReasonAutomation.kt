package me.ghostbear.kumaslash.automation

import dev.kord.common.Color
import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.User
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.kordLogger
import dev.kord.core.on
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.Clock
import me.ghostbear.common.logging.Log
import me.ghostbear.common.logging.Logging
import me.ghostbear.kumaslash.client
import me.ghostbear.kumaslash.commands.logging.toTextChannel
import me.ghostbear.kumaslash.util.Env

suspend fun Kord.timeoutReasonAutomation() {
    val baseUrl = "http://${Env.url}:${Env.port}"
    on<MemberUpdateEvent> {
        val now = Clock.System.now()
        if (
            member.communicationDisabledUntil != null &&
            old?.communicationDisabledUntil != member.communicationDisabledUntil &&
            member.communicationDisabledUntil!! > now
        ) {
            val auditLogsForUser = kord.rest.auditLog.getAuditLogs(member.guildId) {
                action = AuditLogEvent.MemberUpdate
            }

            val entry = auditLogsForUser
                .auditLogEntries
                .firstOrNull { it.targetId == member.id }

            val epoch = member.communicationDisabledUntil?.epochSeconds ?: 0
            val reason = entry?.reason?.value?.replace("\n", "\n> ") ?: "No specific reason was given"
            val moderator = if (entry?.userId != null) {
                rest.user
                    .getUser(entry.userId)
                    .let {
                        User(it.toData(), kord, supplier)
                    }
            } else {
                null
            }

            val timeoutTime = (epoch - (entry?.id?.timestamp?.epochSeconds ?: 0)).toDuration(DurationUnit.SECONDS)
            val timeoutString = when {
                timeoutTime.inWholeSeconds >= 7.days.inWholeSeconds + 10 -> timeoutTime.toString()
                timeoutTime.inWholeSeconds >= 7.days.inWholeSeconds -> "1 week"
                timeoutTime.inWholeSeconds >= 1.days.inWholeSeconds -> "1 day"
                timeoutTime.inWholeSeconds >= 1.hours.inWholeSeconds -> "1 hour"
                timeoutTime.inWholeSeconds >= 10.minutes.inWholeSeconds -> "10 minutes"
                timeoutTime.inWholeSeconds >= 5.minutes.inWholeSeconds -> "5 minutes"
                timeoutTime.inWholeSeconds >= 1.minutes.inWholeSeconds -> "1 minutes"
                else -> timeoutTime.toString()
            }

            try {
                member
                    .getDmChannel()
                    .createMessage {
                        embed {
                            title = "You were timed out"
                            color = Color(47, 49, 54)
                            field {
                                name = "Reason"
                                value = reason
                            }
                            field {
                                name = "Duration"
                                value = "$timeoutString (until <t:${epoch}:f>)"
                            }
                            field {
                                name = "Moderator"
                                value = moderator?.mention ?: "Unknown"
                            }
                            author {
                                name = "Tachiyomi"
                                icon = "https://cdn.discordapp.com/icons/349436576037732353/a_989e89146244387118ab43b03776a90b.webp"
                                url = "https://discord.gg/tachiyomi"
                            }
                        }
                    }
            } catch (e: Exception) {
                kordLogger.info { "Wasn't able to send timeout reason to user. Most likely due to them not allowing DMs from server members" }
            }

            try {
                val request = client.get("$baseUrl/logging") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        Logging(
                            Log.MOD,
                            member.guildId.value.toLong(),
                            0
                        )
                    )
                }

                if (request.status == HttpStatusCode.OK) {
                    val channel = request.body<Logging>().toTextChannel(kord)
                    kord.rest
                        .channel
                        .createMessage(channel.id) {
                            embed {
                                title = "timeout"
                                description = """
                                **Offender:** ${member.mention}
                                **Duration:** $timeoutString (until <t:${epoch}:f>)
                                **Reason:** $reason
                                **Moderator:** ${moderator?.mention}
                            """.trimIndent()
                                footer {
                                    text = "Offender ID: ${member.id.value}"
                                }
                                color = Color(255, 0, 0)
                            }
                        }
                }
            } catch (e: Exception) {
                kordLogger.error { "Couldn't log to the guild logging channel" }
            }
        }

    }
}