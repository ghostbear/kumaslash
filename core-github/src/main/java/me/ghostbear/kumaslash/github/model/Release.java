package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Release(
		@Nullable
		@JsonProperty("name")
		String name,
		@Nullable
		@JsonProperty("tag_name")
		String tagName,
		@Nullable
		@JsonProperty("html_url")
		String htmlUrl,
		@Nullable
		@JsonProperty("assets")
		List<Asset> assets
) {
}
