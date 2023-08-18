package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Title(
		String romaji,
		String english
) {
}
