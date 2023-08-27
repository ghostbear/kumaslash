package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.kumaslash.data.tachiyomi.TachiyomiExtensionService;
import me.ghostbear.kumaslash.util.Tachiyomi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@DiscordComponent
public class SourceEventHandler {

	private final TachiyomiExtensionService tachiyomiExtensionService;

	@Autowired
	public SourceEventHandler(TachiyomiExtensionService tachiyomiExtensionService) {
		this.tachiyomiExtensionService = tachiyomiExtensionService;
	}

	@DiscordInteractionProperties
	public String getName() {
		return "commands/source.json";
	}

	@DiscordInteractionHandler(name = "source")
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(Mono.defer(() -> Mono.just(
						event.getOption("source_id")
								.flatMap(ApplicationCommandInteractionOption::getValue)
								.map(ApplicationCommandInteractionOptionValue::asString)
								.orElseThrow())))
				.flatMap(sourceId -> tachiyomiExtensionService.findById(sourceId))
				.zipWhen(source -> tachiyomiExtensionService.findByPackageName(source.packageName()))
				.flatMap(source -> event.createFollowup()
						.withEmbeds(EmbedCreateSpec.builder()
								.color(Tachiyomi.color())
								.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/icon/%s.png".formatted(StringUtils.substringBeforeLast(source.getT2().fileName(), ".")))
								.title(source.getT1().name())
								.description("""
										Id: %s
										Language: %s
										Version: %s
										""".formatted(source.getT1().getId(), source.getT1().language(), source.getT1().versionId()))
								.build())
						.withComponents(ActionRow.of(Button.link("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/" + source.getT2().fileName(), "Download"))))
				.switchIfEmpty(Mono.defer(() -> event.createFollowup()
						.withEmbeds(EmbedCreateSpec.builder()
								.color(Tachiyomi.color())
								.title("No sources found with that id")
								.description("There was no source found with that id. This could be because either it doesn't exists or that it has been removed due to being to hard to maintain or the scanlator asked to be removed.")
								.build())))
				.onErrorResume(throwable -> event.createFollowup()
						.withContent(throwable.getMessage()))
				.then();
	}
}
