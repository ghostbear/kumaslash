package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Media(
		Long id,
		Type type,
		Format format,
		Title title,
		String description,
		FuzzyDate startDate,
		String status,
		CoverImage coverImage,
		List<String> genres,
		String siteUrl
) {

	public enum Type {
		ANIME,
		MANGA
	}

	public enum Format {
		TV,
		TV_SHORT,
		MOVIE,
		SPECIAL,
		OVA,
		ONA,
		MUSIC,
		MANGA,
		NOVEL,
		ONE_SHOT
	}

}
