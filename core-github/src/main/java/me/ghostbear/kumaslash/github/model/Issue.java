package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Issue(
		@Nullable
		@JsonProperty("state")
		String state,
		@Nullable
		@JsonProperty("state_reason")
		String stateReason,
		@Nullable
		@JsonProperty("html_url")
		String htmlUrl,
		@Nullable
		@JsonProperty("draft")
		Boolean draft,
		@Nullable
		@JsonProperty("number")
		Long number,
		@Nullable
		@JsonProperty("title")
		String title,
		@Nullable
		@JsonProperty("body")
		String body,
		@Nullable
		@JsonProperty("pull_request")
		PullRequest pullRequest,
		@Nullable
		@JsonProperty("user")
		User user
) {
}
