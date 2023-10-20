package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
		@Nullable
		@JsonProperty("login")
		String login,
		@Nullable
		@JsonProperty("avatar_url")
		String avatarUrl
) {
}
