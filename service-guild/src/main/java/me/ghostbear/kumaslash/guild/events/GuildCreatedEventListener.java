package me.ghostbear.kumaslash.guild.events;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.kumaslash.guild.repositories.GuildRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@DiscordComponent
public class GuildCreatedEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(GuildCreatedEventListener.class);

	@NotNull
	private final GuildRepository guildRepository;

	@Autowired
	public GuildCreatedEventListener(@NotNull GuildRepository guildRepository) {
		this.guildRepository = Objects.requireNonNull(guildRepository);
	}

	@DiscordEventHandler
	public Publisher<?> onGuildCreate(@NotNull GuildCreateEvent event) {
		Snowflake snowflake = event.getGuild().getId();
		return guildRepository.saveOrDoNothing(snowflake.asLong())
				.doOnSuccess(guild -> LOG.debug("Registered guild with snowflake {}", snowflake.asString()))
				.doOnError(throwable -> LOG.error("Failed to register guild with snowflake {}", snowflake.asString()))
				.onErrorContinue((throwable, object) -> LOG.error("Critical error trying to register guild", throwable));
	}

}
