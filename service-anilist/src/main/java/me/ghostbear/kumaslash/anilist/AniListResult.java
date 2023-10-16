package me.ghostbear.kumaslash.anilist;

import me.ghostbear.kumaslash.anilist.model.Media;

public sealed interface AniListResult {
	record Success(Media media) implements AniListResult {
	}

	record NotFound(String query) implements AniListResult {
	}

	record UnknownError(String query, Throwable error) implements AniListResult {
	}
}
