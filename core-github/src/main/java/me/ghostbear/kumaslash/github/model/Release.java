package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Release(
		@JsonProperty("name")
		String name,
		@JsonProperty("tag_name")
		String tagName,
		@JsonProperty("html_url")
		String htmlUrl,
		@JsonProperty("assets")
		List<Asset> assets
) {
}
