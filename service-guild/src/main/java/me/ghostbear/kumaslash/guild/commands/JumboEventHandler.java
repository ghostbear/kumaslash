package me.ghostbear.kumaslash.guild.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.rest.util.Image;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DiscordComponent
public class JumboEventHandler {

	private final Pattern pattern = Pattern.compile("<(a)?:(.*):(.*)>", Pattern.CASE_INSENSITIVE);

	@DiscordInteractionProperties
	public String applicationProperties() {
		return "commands/jumbo.json";
	}

	@DiscordInteractionHandler(name = "jumbo")
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return Mono.justOrEmpty(event.getOption("emoji")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asString))
				.map(emoji -> pattern.matcher(emoji))
				.filter(matcher -> matcher.find())
				.zipWhen(matcher -> extractImageFormat(matcher))
				.flatMap(matcherAndFormat -> createJumboReply(event, matcherAndFormat))
				.switchIfEmpty(Mono.defer(() -> createNoEmojiReply(event)))
				.then();
	}

	Mono<Image.Format> extractImageFormat(Matcher matcher) {
		if (Objects.equals(matcher.group(1), "a")) return Mono.just(Image.Format.GIF);
		return Mono.just(Image.Format.PNG);
	}

	Mono<Boolean> createJumboReply(ChatInputInteractionEvent event, Tuple2<Matcher, Image.Format> matcherAndFormat) {
		String template = "https://cdn.discordapp.com/emojis/%s.%s?v=1";
		return event.reply(template.formatted(matcherAndFormat.getT1().group(3), matcherAndFormat.getT2().getExtension())).thenReturn(matcherAndFormat)
				.thenReturn(true);
	}

	Mono<Boolean> createNoEmojiReply(ChatInputInteractionEvent event) {
		return event.reply("No emoji found in data")
				.withEphemeral(true)
				.thenReturn(true);
	}
}
