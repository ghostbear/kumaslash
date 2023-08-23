package me.ghostbear.core.discord4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

	private Bot bot;

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public static class Bot {

		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

}
