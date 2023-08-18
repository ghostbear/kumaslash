package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FuzzyDate(
		Long year,
		Long month,
		Long day
) {

	@Override
	public String toString() {
		return "%s/%s/%s".formatted(year, month, day);
	}
}
