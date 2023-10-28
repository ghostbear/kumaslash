package me.ghostbear.core.discord4j;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import me.ghostbear.core.discord4j.beans.DiscordApplicationCommandRegistrar;
import me.ghostbear.core.discord4j.beans.DiscordEventHandlerBeanProcessor;
import org.jetbrains.annotations.NotNull;
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
	public DiscordClient discordClient(@NotNull DiscordProperties discordProperties) {
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
	@DependsOn({"restClient", "discordEventHandlerBeanProcessor"})
	public DiscordApplicationCommandRegistrar discordApplicationCommandRegistrarBeanProcessor(ApplicationContext applicationContext, RestClient restClient) {
		return new DiscordApplicationCommandRegistrar(applicationContext, restClient);
	}

	@Bean("gatewayDiscordClient")
	@DependsOn({"discordClient", "eventDispatcher", "discordEventHandlerBeanProcessor"})
	@ConditionalOnMissingBean
	public GatewayDiscordClient gatewayDiscordClient(@NotNull DiscordClient discordClient, EventDispatcher eventDispatcher, IntentSet intentSet) {
		return discordClient.gateway()
				.setEventDispatcher(eventDispatcher)
				.setEnabledIntents(intentSet)
				.login()
				.doOnNext(gatewayDiscordClient -> {
					Thread awaitThread = new Thread(() -> gatewayDiscordClient.onDisconnect().block(), "discord");
					awaitThread.setContextClassLoader(getClass().getClassLoader());
					awaitThread.setDaemon(false);
					awaitThread.start();
				})
				.block();
	}

	@Bean
	@DependsOn({"gatewayDiscordClient"})
	RestClient restClient(@NotNull GatewayDiscordClient gatewayDiscordClient) {
		return gatewayDiscordClient.getRestClient();
	}

	@Bean("intentSet")
	@ConditionalOnMissingBean
	public IntentSet intentSet() {
		return IntentSet.nonPrivileged();
	}
}
