package me.ghostbear.kumaslash.anilist;

import discord4j.gateway.intent.IntentSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import static discord4j.gateway.intent.Intent.MESSAGE_CONTENT;

@EnableScheduling
@SpringBootApplication
public class KumaslashAniListApplication {

	public static void main(String[] args) {
		SpringApplication.run(KumaslashAniListApplication.class, args);
	}

	@Bean
	public IntentSet intentSet() {
		return IntentSet.nonPrivileged()
				.or(IntentSet.of(MESSAGE_CONTENT));
	}
}
