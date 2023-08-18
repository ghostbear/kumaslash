package me.ghostbear.kumaslash.anilist;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static discord4j.gateway.intent.Intent.MESSAGE_CONTENT;

@Configuration
public class DiscordBotConfiguration {

	@Value("${discord.bot.token}")
	private String token;

	@Bean
	public GatewayDiscordClient gatewayDiscordClient() {
		return DiscordClientBuilder.create(token)
				.build()
				.gateway()
				.setEnabledIntents(IntentSet.nonPrivileged().or(IntentSet.of(MESSAGE_CONTENT)))
				.login()
				.block();
	}

	@Bean
	public RestClient discordRestClient(GatewayDiscordClient gatewayDiscordClient) {
		return gatewayDiscordClient.getRestClient();
	}

}
