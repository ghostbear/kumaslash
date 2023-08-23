package me.ghostbear.core.discord4j;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import me.ghostbear.core.discord4j.beans.DiscordApplicationCommandRegistrar;
import me.ghostbear.core.discord4j.beans.DiscordEventHandlerBeanProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties(DiscordProperties.class)
public class DiscordAutoConfiguration {

	@Bean("discordClient")
	@ConditionalOnMissingBean
	public DiscordClient discordClient(DiscordProperties discordProperties) {
		return DiscordClientBuilder.create(discordProperties.getBot().getToken())
				.build();
	}

	@Bean("eventDispatcher")
	@ConditionalOnMissingBean
	public EventDispatcher eventDispatcher() {
		return EventDispatcher.builder()
				.build();
	}

	@Bean("discordEventHandlerBeanProcessor")
	@ConditionalOnMissingBean
	public DiscordEventHandlerBeanProcessor discordEventHandlerBeanProcessor(EventDispatcher eventDispatcher) {
		return new DiscordEventHandlerBeanProcessor(eventDispatcher);
	}

	@Bean(value = "discordApplicationCommandRegistrarBeanProcessor")
	@DependsOn({"gatewayDiscordClient", "discordEventHandlerBeanProcessor"})
	public DiscordApplicationCommandRegistrar discordApplicationCommandRegistrarBeanProcessor(ApplicationContext applicationContext, GatewayDiscordClient gatewayDiscordClient) {
		return new DiscordApplicationCommandRegistrar(applicationContext, gatewayDiscordClient);
	}

	@Bean("gatewayDiscordClient")
	@DependsOn({"discordClient", "eventDispatcher", "discordEventHandlerBeanProcessor"})
	@ConditionalOnMissingBean
	public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient, EventDispatcher eventDispatcher) {
		return discordClient.gateway()
				.setEventDispatcher(eventDispatcher)
				.login()
				.block();
	}
}
