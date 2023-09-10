package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequest(
		@JsonProperty("html_url")
		String htmlUrl,
		@JsonProperty("merged_at")
		String mergedAt
) {

	public boolean isMerged() {
		return Objects.nonNull(mergedAt);
	}

}
