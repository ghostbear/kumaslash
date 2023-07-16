package me.ghostbear.kumaslash.data.tachiyomi.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SourceDTO(
		String name,
		String lang,
		String id,
		String baseUrl,
		int versionId,
		int hasCloudflare
) {
}
