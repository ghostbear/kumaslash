package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Media(
		@NotNull
		Long id,
		@Nullable
		Type type,
		@Nullable
		Format format,
		@Nullable
		Title title,
		@Nullable
		String description,
		@Nullable
		FuzzyDate startDate,
		@Nullable
		MediaStatus status,
		@Nullable
		CoverImage coverImage,
		@Nullable
		List<String> genres,
		@Nullable
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
