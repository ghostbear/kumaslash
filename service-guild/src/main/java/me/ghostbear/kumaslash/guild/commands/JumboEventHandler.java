package me.ghostbear.kumaslash.guild.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateFields;
import discord4j.rest.util.Image;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DiscordComponent
public class JumboEventHandler {

	private final Pattern pattern = Pattern.compile("<(a)?:(.*):(.*)>", Pattern.CASE_INSENSITIVE);
	private final String template = "https://cdn.discordapp.com/emojis/%s.%s?v=1";

	private final WebClient webClient;

	public JumboEventHandler(WebClient webClient) {
		this.webClient = webClient;
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
				.flatMap(JumboEventHandler.this::findAndGetEmojiOrEmpty)
				.flatMap(s -> event.deferReply().thenReturn(s))
				.flatMap(t -> getEmojiAsInputStream(t.getT2()).map(is -> Tuples.of(t.getT1(), is)))
				.flatMap(t -> successReply(event, t.getT1(), t.getT2()))
				.switchIfEmpty(Mono.defer(() -> emptyReply(event)));
	}

	Mono<Tuple2<String, String>> findAndGetEmojiOrEmpty(String emoji) {
		return Mono.just(emoji)
				.map(pattern::matcher)
				.filter(Matcher::find)
				.map(matcher -> {
					Image.Format format = Objects.equals(matcher.group(1), "a") ? Image.Format.GIF : Image.Format.PNG;
					String name = "%s.%s".formatted(matcher.group(2), format.getExtension());
					String url = template.formatted(matcher.group(3), format.getExtension());
					return Tuples.of(name, url);
				});
	}

	Mono<InputStream> getEmojiAsInputStream(String url) {
		return webClient.get()
				.uri(url)
				.retrieve()
				.bodyToFlux(DataBuffer.class)
				.map(b -> b.asInputStream(true))
				.reduce(SequenceInputStream::new);
	}


	Mono<Message> successReply(ChatInputInteractionEvent event, String filename, InputStream inputStream) {
		return event.createFollowup()
				.withFiles(MessageCreateFields.File.of(filename, inputStream));
	}

	Mono<Message> emptyReply(ChatInputInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(event.createFollowup()
						.withContent("No emoji found in option"));
	}
}
