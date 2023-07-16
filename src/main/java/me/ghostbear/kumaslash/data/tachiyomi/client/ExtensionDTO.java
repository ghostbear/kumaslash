package me.ghostbear.kumaslash.data.tachiyomi.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExtensionDTO(
		String name,
		String pkg,
		String apk,
		String lang,
		int code,
		String version,
		int nsfw,
		int hasReadme,
		int hasChangelog,
		List<SourceDTO> sources
) {
}

