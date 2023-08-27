package me.ghostbear.kumaslash.anilist;

import discord4j.gateway.intent.IntentSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static discord4j.gateway.intent.Intent.MESSAGE_CONTENT;

@Configuration
public class DiscordBotConfiguration {

	@Bean
	public IntentSet intentSet() {
		return IntentSet.nonPrivileged()
				.or(IntentSet.of(MESSAGE_CONTENT));
	}

}
