package me.ghostbear.kumaslash.configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import me.ghostbear.kumaslash.commands.GuildCreatedEventHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.core.publisher.Flux;

@Configuration
public class DiscordBotConfiguration {

	@Value("${discord.bot.token}")
	private String token;

	@Bean
	@DependsOn({"flyway"})
	public GatewayDiscordClient gatewayDiscordClient(GuildCreatedEventHandler guildCreatedEventHandler) {
		GatewayDiscordClient gatewayDiscordClient = DiscordClientBuilder.create(token)
				.build()
				.gateway()
				.setAwaitConnections(true)
				.setEnabledIntents(
						IntentSet.of(
								Intent.GUILDS,
								Intent.GUILD_MESSAGES,
								Intent.GUILD_MODERATION,
								Intent.GUILD_MEMBERS,
								Intent.AUTO_MODERATION_EXECUTION))
				.login()
				.block();
		gatewayDiscordClient.on(guildCreatedEventHandler).subscribe();
		return gatewayDiscordClient;
	}

	@Bean
	public RestClient discordRestClient(GatewayDiscordClient gatewayDiscordClient) {
		return gatewayDiscordClient.getRestClient();
	}

}
