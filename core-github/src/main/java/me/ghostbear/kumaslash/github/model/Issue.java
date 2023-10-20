package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Issue(
		@JsonProperty("state")
		String state,
		@JsonProperty("state_reason")
		String stateReason,
		@JsonProperty("html_url")
		String htmlUrl,
		@JsonProperty("draft")
		Boolean draft,
		@JsonProperty("number")
		Long number,
		@JsonProperty("title")
		String title,
		@JsonProperty("body")
		String body,
		@JsonProperty("pull_request")
		PullRequest pullRequest,
		@JsonProperty("user")
		User user
) {
}
