package me.ghostbear.kumaslash.guild.utils;

import discord4j.rest.util.Image;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmojiPatternMatcher {

	private final Pattern pattern = Pattern.compile("<(a)?:(.*):(.*)>", Pattern.CASE_INSENSITIVE);

	public Optional<Match> findAndGetEmojiOrEmpty(String emoji) {
		Matcher matcher = pattern.matcher(emoji);
		if (!matcher.find()) return Optional.empty();
		Image.Format format = Objects.equals(matcher.group(1), "a") ? Image.Format.GIF : Image.Format.PNG;
		return Optional.of(new Match(matcher.group(2), format.getExtension()));
	}

	public record Match(String name, String extension) {
	}

}
