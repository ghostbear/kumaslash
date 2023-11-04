package me.ghostbear.kumaslash.guild.events;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.AuditLogEntryCreateEvent;
import discord4j.core.event.domain.automod.AutoModActionExecutedEvent;
import discord4j.core.object.audit.ActionType;
import discord4j.core.object.audit.AuditLogChange;
import discord4j.core.object.audit.AuditLogEntry;
import discord4j.core.object.audit.ChangeKey;
import discord4j.core.object.automod.AutoModRuleAction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateMono;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.AuditLogData;
import discord4j.discordjson.json.AuditLogEntryData;
import discord4j.discordjson.json.UserData;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.service.AuditLogService;
import discord4j.rest.service.ChannelService;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.kumaslash.guild.repositories.ChannelRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@DiscordComponent
public class TimeoutEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(TimeoutEventListener.class);

	private final Function<Id, String> asMention = value -> "<@%s>".formatted(value.asString());

	private final GatewayDiscordClient gateway;
	private final AuditLogService auditLogService;
	private final ChannelService channelService;
	private final ChannelRepository channelRepository;

	public TimeoutEventListener(GatewayDiscordClient client, ChannelRepository channelRepository) {
		gateway = client;
		auditLogService = client.getRestClient()
				.getAuditLogService();
		channelService = client.getRestClient()
				.getChannelService();
		this.channelRepository = channelRepository;
	}

	@DiscordEventHandler
	public Publisher<?> onAuditLogEntryCreate(AuditLogEntryCreateEvent event) {
		AuditLogEntry auditLogEntry = event.getAuditLogEntry();
		if (!auditLogEntry.getActionType().equals(ActionType.MEMBER_UPDATE)) {
			return Mono.empty();
		}
		LOG.info("Audit Log Entry Create is MEMBER UPDATE");
		Optional<AuditLogChange<Instant>> changeOptional = auditLogEntry.getChange(ChangeKey.COMMUNICATION_DISABLED_UNTIL);
		if (changeOptional.isEmpty()) {
			return Mono.empty();
		}
		AuditLogChange<Instant> change = changeOptional.get();
		if (change.getCurrentValue().isEmpty()) {
			return Mono.empty();
		}
		if (change.getCurrentValue().orElse(Instant.now()).isBefore(change.getOldValue().orElse(Instant.ofEpochMilli(0)))) {
			return Mono.empty();
		}

		Duration duration = Duration.between(event.getAuditLogEntry().getId().getTimestamp(), change.getCurrentValue().orElse(Instant.EPOCH));

		LOG.info("Audit Log Entry Create is for COMMUNICATION DISABLED UNTIL");

		AuditLogData auditLogData = auditLogService.getAuditLog(
						event.getGuildId().asLong(),
						Map.of("action_type", ActionType.MEMBER_UPDATE.getValue()))
				.block();

		AuditLogEntry eventAuditLogEntry = event.getAuditLogEntry();
		Optional<AuditLogEntryData> auditLogEntryData = auditLogData.auditLogEntries()
				.stream()
				.filter(data -> data.id().asString().equals(eventAuditLogEntry.getId().asString()))
				.findFirst();


		Optional<UserData> responsibleUser = auditLogEntryData.flatMap(AuditLogEntryData::userId)
				.flatMap(value -> auditLogData.users().stream().filter(userData -> userData.id().equals(value)).findFirst());
		if (responsibleUser.isEmpty()) {
			LOG.warn("Couldn't find the responsible user");
			return Mono.empty();
		}
		Optional<UserData> targetUser = auditLogEntryData.flatMap(AuditLogEntryData::targetId)
				.flatMap(value -> auditLogData.users().stream().filter(userData -> userData.id().asString().equals(value)).findFirst());
		if (targetUser.isEmpty()) {
			LOG.warn("Couldn't find the target user");
			return Mono.empty();
		}
		String reason = auditLogEntryData.map(AuditLogEntryData::reason)
				.flatMap(Possible::toOptional)
				.orElse("No reason provided");

		return getGuildLogChannel(event.getGuildId().asLong())
				.flatMap(channel -> createTimeoutChannelMessage(channel, targetUser, duration, change.getCurrentValue().orElse(Instant.now()), reason, responsibleUser))
				.then(Mono.defer(() -> new User(gateway, targetUser.get()).getPrivateChannel()
						.flatMap(privateChannel -> createPrivateChannelMessage(event.getGuild().block(), privateChannel, change.getCurrentValue().orElse(Instant.now()), reason))));
	}

	@DiscordEventHandler
	public Publisher<?> onAutoModActionExecution(AutoModActionExecutedEvent event) {
		AutoModRuleAction eventAction = event.getAction();
		if (!eventAction.getType().equals(AutoModRuleAction.Type.TIMEOUT)) {
			return Mono.empty();
		}
		LOG.info("Auto Mod Action Execution is of type TIMEOUT");

		AuditLogData auditLogData = auditLogService.getAuditLog(
						event.getGuildId().asLong(),
						Map.of("action_type", ActionType.AUTO_MODERATION_USER_COMMUNICATION_DISABLED.getValue()))
				.block();

		Optional<AuditLogEntryData> auditLogEntryData = auditLogData.auditLogEntries().stream()
				.filter(entry -> entry.actionType() == ActionType.AUTO_MODERATION_USER_COMMUNICATION_DISABLED.getValue())
				.filter(entry -> entry.targetId().orElse("").equals(event.getUserId().asString()))
				.findFirst();

		Optional<UserData> targetUser = auditLogEntryData.flatMap(AuditLogEntryData::targetId)
				.flatMap(value -> auditLogData.users().stream().filter(userData -> userData.id().asString().equals(value)).findFirst());
		if (targetUser.isEmpty()) {
			LOG.warn("Couldn't find the target user");
			return Mono.empty();
		}
		String reason = auditLogEntryData.map(AuditLogEntryData::reason)
				.flatMap(Possible::toOptional)
				.orElse("No reason provided");

		Instant instant = event.getUser()
				.flatMap(user -> user.asMember(event.getGuildId()))
				.map(PartialMember::getCommunicationDisabledUntil)
				.block()
				.orElse(Instant.now());

		return getGuildLogChannel(event.getGuildId().asLong())
				.flatMap(channel -> createTimeoutChannelMessage(channel, targetUser.map(UserData::id).map(asMention).orElseThrow(), Duration.between(event.getMessageId().map(Snowflake::getTimestamp).orElse(Instant.EPOCH), instant), instant, reason, "AutoMod"))
				.then(Mono.defer(() -> event.getUser()
						.flatMap(User::getPrivateChannel)
						.flatMap(privateChannel -> createPrivateChannelMessage(event.getGuild().block(), privateChannel, instant, reason))));
	}

	private MessageCreateMono createTimeoutChannelMessage(GuildMessageChannel channel, Optional<UserData> targetUser, Duration change, Instant instant, String reason, Optional<UserData> responsibleUser) {
		return createTimeoutChannelMessage(channel, targetUser.map(UserData::id).map(asMention).orElseThrow(), change, instant, reason, responsibleUser.map(UserData::id).map(asMention).orElseThrow());
	}

	private MessageCreateMono createTimeoutChannelMessage(GuildMessageChannel channel, String targetUser, Duration duration, Instant instant, String reason, String responsibleUser) {
		return channel.createMessage(
				EmbedCreateSpec.builder()
						.title("timeout")
						.description("""
								Offender: %s
								Duration: %s <t:%s:f>
								Reason: %s
								Moderator: %s
								""".formatted(targetUser,
								formatDuration(duration),
								instant.getEpochSecond(),
								reason,
								responsibleUser))
						.build());
	}

	private String formatDuration(Duration duration) {
		StringBuilder builder = new StringBuilder();
		if (duration.toDays() == 7) {
			builder.append("1 week ");
		} else if (duration.toDays() == 1) {
			builder.append("1 day ");
		} else if (duration.toHoursPart() == 1) {
			builder.append("1 hour ");
		} else if (duration.toMinutesPart() == 10) {
			builder.append("10 minutes ");
		} else if (duration.toMinutesPart() == 5) {
			builder.append("5 minutes ");
		} else if (duration.toMinutesPart() > 0) {
			builder.append("1 minute ");
		} else {
			builder.append(duration);
		}
		return builder.toString().trim();
	}

	private static MessageCreateMono createPrivateChannelMessage(Guild guild, PrivateChannel privateChannel, Instant instant, String reason) {
		return privateChannel.createMessage("""
				You've been timed out until <t:%s:f> in %s with the following reason:
				> %s
				""".formatted(
				instant.getEpochSecond(),
				guild.getName(),
				reason));
	}


	private Mono<GuildMessageChannel> getGuildLogChannel(long guildSnowflake) {
		return channelRepository.findByGuildSnowflake(guildSnowflake)
				.flatMap(guildMessageChannel -> channelService.getChannel(guildMessageChannel.channelSnowflake()))
				.filter(channelData -> channelData.type() == Channel.Type.GUILD_TEXT.getValue())
				.map(channelData -> new TextChannel(gateway, channelData));
	}

}
