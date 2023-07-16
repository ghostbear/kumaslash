package me.ghostbear.kumaslash.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.GuildUpdateEvent;
import me.ghostbear.kumaslash.data.guild.Guild;
import me.ghostbear.kumaslash.data.guild.GuildRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class GuildCreatedEventHandler extends ReactiveEventAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(GuildCreatedEventHandler.class);

	private final GuildRepository guildRepository;

	@Autowired
	public GuildCreatedEventHandler(GuildRepository guildRepository) {
		this.guildRepository = guildRepository;
	}

	@Override
	public Publisher<?> onGuildCreate(GuildCreateEvent event) {
		Snowflake snowflake = event.getGuild().getId();
		LOG.info("Guild Update: {}", event);
		return guildRepository.findById(snowflake.asLong())
				.delayElement(Duration.ofSeconds(0))
				.switchIfEmpty(Mono.defer(() -> guildRepository.save(new Guild(snowflake, true))))
				.doOnError(throwable -> LOG.error("Failed to create new guild", throwable))
				.then();
	}

	@Override
	public Publisher<?> onGuildUpdate(GuildUpdateEvent event) {
		LOG.info("Guild Update: {}", event);
		return super.onGuildUpdate(event);
	}

	@Override
	public Publisher<?> onGuildDelete(GuildDeleteEvent event) {
		LOG.info("Guild Delete: {}", event);
		return super.onGuildDelete(event);
	}
}
