package me.ghostbear.kumaslash.anilist;

import discord4j.gateway.intent.IntentSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

import static discord4j.gateway.intent.Intent.MESSAGE_CONTENT;

@Configuration
public class DiscordBotConfiguration {

	private final DispatcherHandler webHandler;

	public DiscordBotConfiguration(DispatcherHandler webHandler) {
		this.webHandler = webHandler;
	}

	@Bean
	public IntentSet intentSet() {
		return IntentSet.nonPrivileged()
				.or(IntentSet.of(MESSAGE_CONTENT));
	}



}
