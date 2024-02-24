/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist.model;

import java.util.List;

public record Media(
		Long id,
		Type type,
		Format format,
		Boolean isAdult,
		MediaTitle title,
		String description,
		FuzzyDate startDate,
		CoverImage coverImage,
		MediaStatus status,
		List<String> genres,
		String siteUrl) {

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
