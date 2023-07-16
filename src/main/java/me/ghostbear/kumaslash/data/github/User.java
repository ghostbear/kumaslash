package me.ghostbear.kumaslash.data.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
		@JsonProperty("login")
		String login,
		@JsonProperty("avatar_url")
		String avatarUrl
) {
}
