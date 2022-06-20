package me.ghostbear.kumaslash.automation

import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.on
import kotlinx.datetime.Clock

suspend fun Kord.timeoutReasonAutomation() {
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

            val epoch = member.communicationDisabledUntil?.epochSeconds
            val reason = entry?.reason?.value?.replace("\n", "\n> ") ?: "No specific reason was given"

            member
                .getDmChannel()
                .createMessage {
                    content =
                        "You've been timed out until <t:$epoch:f> in Tachiyomi with the following reason:\n> $reason"
                }
        }

    }
}