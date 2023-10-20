package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Asset(
		@JsonProperty("name")
		String name,
		@JsonProperty("browser_download_url")
		String browserDownloadUrl
) {
}
