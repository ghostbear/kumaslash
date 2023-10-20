package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequest(
		@Nullable
		@JsonProperty("html_url")
		String htmlUrl,
		@Nullable
		@JsonProperty("merged_at")
		String mergedAt
) {

	public boolean isMerged() {
		return Objects.nonNull(mergedAt);
	}

}
