package me.ghostbear.kumaslash.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Asset(
		@Nullable
		@JsonProperty("name")
		String name,
		@Nullable
		@JsonProperty("browser_download_url")
		String browserDownloadUrl
) {
}
