package me.ghostbear.kumaslash.guild.controllers;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateFields;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.kumaslash.guild.services.EmojiService;
import me.ghostbear.kumaslash.guild.utils.EmojiPatternMatcher;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.io.InputStream;
import java.util.Optional;

@DiscordComponent
public class JumboController {

	private final EmojiPatternMatcher emojiPatternMatcher;
	private final EmojiService emojiService;

	public JumboController(EmojiPatternMatcher emojiPatternMatcher, EmojiService emojiService) {
		this.emojiPatternMatcher = emojiPatternMatcher;
		this.emojiService = emojiService;
	}

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/jumbo.json");
	}

	@DiscordInteractionHandler(name = "jumbo")
	public Publisher<?> handle(ChatInputInteractionEvent event) {
		Optional<String> option = event.getOption("emoji")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString);
		return Mono.justOrEmpty(option)
				.flatMap(e -> Mono.justOrEmpty(emojiPatternMatcher.findAndGetEmojiOrEmpty(e)))
				.flatMap(s -> event.deferReply().thenReturn(s))
				.flatMap(t -> emojiService.getEmojiAsInputStream(t.name(), t.extension())
						.map(inputStream -> Tuples.of(t.name(), t.extension(), inputStream)))
				.flatMap(t -> successReply(event, t.getT1(), t.getT2(), t.getT3()))
				.switchIfEmpty(Mono.defer(() -> emptyReply(event)));
	}

	Mono<Message> successReply(ChatInputInteractionEvent event, String name, String extension, InputStream inputStream) {
		return event.createFollowup()
				.withFiles(MessageCreateFields.File.of("%s.%s".formatted(name, extension), inputStream));
	}

	Mono<Message> emptyReply(ChatInputInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(event.createFollowup()
						.withContent("No emoji found in option"));
	}

}
