/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.moderation.timeout;

import kumaslash.core.preferences.Preference;
import kumaslash.jda.annotations.EventMapping;
import kumaslash.jda.annotations.JDAController;
import kumaslash.moderation.ModerationPreferences;

import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

@JDAController
public class TimeoutController {

	private static final Logger LOG = LoggerFactory.getLogger(TimeoutController.class);

	public static DateTimeFormatter DATE_TIME = new DateTimeFormatterBuilder()
			.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
			.appendOffsetId()
			.toFormatter();

	private final ModerationPreferences preferences;

	public TimeoutController(ModerationPreferences preferences) {
		this.preferences = preferences;
	}

	@EventMapping
	public void onGuildAuditLogEntryCreateEvent(GuildAuditLogEntryCreateEvent event) {
		AuditLogEntry entry = event.getEntry();
		AuditLogChange auditLogChange = entry.getChangeByKey(AuditLogKey.MEMBER_TIME_OUT);
		if (Objects.isNull(auditLogChange)) {
			return;
		}
		if (!AuditLogKey.MEMBER_TIME_OUT.getKey().equals(auditLogChange.getKey())) {
			return;
		}

		String newValue = auditLogChange.getNewValue();
		if (Objects.isNull(newValue)) {
			return;
		}
		if (Objects.equals(newValue, auditLogChange.getOldValue())) {
			return;
		}

		OffsetDateTime timeCreated = TimeUtil.getTimeCreated(entry.getIdLong());
		OffsetDateTime dateTime = LocalDateTime.parse(newValue, DATE_TIME).atOffset(ZoneOffset.UTC);
		Duration between = Duration.between(timeCreated, dateTime);

		String duration = getTimeoutDurationAsString(between);

		Guild guild = event.getGuild();
		Preference<Long> moderationChannel = preferences.moderationChannel(guild);

		if (!moderationChannel.isSet()) {
			LOG.error("{} hasn't set their moderation channel", guild.getId());
			return;
		}
		Long channelId = moderationChannel.get();

		TextChannel textChannel = guild.getTextChannelById(channelId);
		if (Objects.isNull(textChannel) || !textChannel.canTalk()) {
			LOG.error("Either channel ({}) doesn't exists or bot can't talk in it", channelId);
			return;
		}
		textChannel
				.sendMessageFormat(
						"""
										Timeout duration: %s
										Reason: %s
										Target: <@%s>
										Provoked by: <@%s>
										""",
						duration, entry.getReason(), entry.getTargetId(), entry.getUserId())
				.queue();

		Member guildMember = guild.getMember(UserSnowflake.fromId(entry.getTargetId()));
		if (Objects.isNull(guildMember)) {
			LOG.error("Couldn't find guild member ({})", entry.getTargetId());
			return;
		}
		guildMember
				.getUser()
				.openPrivateChannel()
				.flatMap(privateChannel -> {
					if (privateChannel.canTalk()) {
						return privateChannel.sendMessageFormat(
								"""
										Timeout duration: %s
										Reason: %s
										Target: <@%s>
										Provoked by: <@%s>
										""",
								duration,
								entry.getReason(),
								entry.getTargetId(),
								entry.getUserId());
					}
					return null;
				})
				.queue();
	}

	@EventMapping
	public void onAutoModExecutionEvent(AutoModExecutionEvent event) {
		AutoModResponse response = event.getResponse();
		System.out.println("event = " + event);
		if (!Objects.equals(response.getType(), AutoModResponse.Type.TIMEOUT)) {
			return;
		}
		Duration between = response.getTimeoutDuration();
		if (Objects.isNull(between)) {
			return;
		}
		String duration = getTimeoutDurationAsString(response.getTimeoutDuration());
		String message = response.getCustomMessage();

		Preference<Long> moderationChannel = preferences.moderationChannel(event.getGuild());
		Long channelId = moderationChannel.get();

		if (Objects.isNull(channelId)) {
			LOG.error("{} hasn't set their moderation channel", event.getGuild().getId());
			return;
		}

		event.getGuild()
				.getTextChannelById(channelId)
				.sendMessageFormat(
						"""
										Timeout duration: %s
										Reason: %s
										Target: <@%s>
										Provoked by: AutoMod
										""",
						duration, message, event.getUserId())
				.queue();

		event.getJDA()
				.getUserById(event.getUserId())
				.openPrivateChannel()
				.flatMap(privateChannel -> {
					if (privateChannel.canTalk()) {
						return privateChannel.sendMessageFormat(
								"""
												Timeout duration: %s
												Reason: %s
												Target: <@%s>
												Provoked by: AutoMod
												""",
								duration, message, event.getUserId());
					}
					return null;
				})
				.queue();
	}

	private static String getTimeoutDurationAsString(Duration between) {
		String duration = "";
		if (between.toDays() > 1) {
			duration = between.toDays() + " days";
		} else if (between.toDays() == 1) {
			duration = between.toDays() + " day";
		} else if (between.toHours() > 1) {
			duration = between.toHours() + " hours";
		} else if (between.toHours() == 1) {
			duration = between.toHours() + " hour";
		} else if (between.toMinutes() > 1) {
			duration = between.toMinutes() + " minutes";
		} else if (between.toMinutes() == 1) {
			duration = between.toMinutes() + " minute";
		} else {
			duration = "Duration malformed";
		}
		return duration;
	}
}
