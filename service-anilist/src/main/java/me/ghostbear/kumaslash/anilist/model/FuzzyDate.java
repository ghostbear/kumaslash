package me.ghostbear.kumaslash.anilist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FuzzyDate(
		Long year,
		Long month,
		Long day
) {

	@Override
	public String toString() {
		return Arrays.stream(new Long[]{year, month, day})
				.filter(Objects::nonNull)
				.map(Object::toString)
				.collect(Collectors.joining("/"));
	}
}
