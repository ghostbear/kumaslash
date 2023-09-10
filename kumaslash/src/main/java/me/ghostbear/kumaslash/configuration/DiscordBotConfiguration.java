package me.ghostbear.kumaslash.configuration;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class DiscordBotConfiguration {

	@Bean("gatewayDiscordClient")
	@DependsOn({"discordClient", "eventDispatcher", "discordEventHandlerBeanProcessor"})
	public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient, EventDispatcher eventDispatcher, IntentSet intentSet) {
		return discordClient.gateway()
				.setEventDispatcher(eventDispatcher)
				.setEnabledIntents(intentSet)
				.login()
				.block();
	}

	@Bean
	public IntentSet intentSet() {
		return IntentSet.of(
						Intent.GUILDS,
						Intent.GUILD_MESSAGES,
						Intent.GUILD_MODERATION,
						Intent.GUILD_MEMBERS,
						Intent.AUTO_MODERATION_EXECUTION,
						Intent.MESSAGE_CONTENT);
	}

}
