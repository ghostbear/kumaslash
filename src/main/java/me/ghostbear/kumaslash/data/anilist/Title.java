package me.ghostbear.kumaslash.data.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Title(
		String romaji,
		String english
) {
}
