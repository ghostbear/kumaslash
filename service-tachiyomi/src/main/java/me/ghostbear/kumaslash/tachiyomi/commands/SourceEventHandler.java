package me.ghostbear.kumaslash.tachiyomi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.tachiyomi.TachiyomiExtensionService;
import me.ghostbear.kumaslash.tachiyomi.local.model.Extension;
import me.ghostbear.kumaslash.tachiyomi.local.model.Source;
import me.ghostbear.kumaslash.tachiyomi.util.TachiyomiHelper;
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
	public Resources commandProperties() {
		return Resources.of("commands/source.json");
	}

	@DiscordInteractionHandler(name = "source")
	public Mono<?> handle(ChatInputInteractionEvent event) {
		String sourceId = event.getOption("source_id")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();
		return event.deferReply()
				.withEphemeral(true)
				.then( Mono.just(sourceId))
				.flatMap(tachiyomiExtensionService::findById)
				.zipWhen(source -> tachiyomiExtensionService.findByPackageName(source.packageName()))
				.flatMap(tuple -> sourceReply(event, tuple.getT1(), tuple.getT2()))
				.switchIfEmpty(Mono.defer(() -> sourceNotFoundReply(event)));
	}

	Mono<Message> sourceReply(ChatInputInteractionEvent event, Source source, Extension extension) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.color(TachiyomiHelper.color())
						.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/icon/%s.png".formatted(StringUtils.substringBeforeLast(extension.fileName(), ".")))
						.title(source.name())
						.description("""
										Id: %s
										Language: %s
										Version: %s
										""".formatted(source.getId(), source.language(), source.versionId()))
						.build())
				.withComponents(ActionRow.of(Button.link("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi-extensions/repo/apk/" + extension.fileName(), "Download")));
	}

	Mono<Message> sourceNotFoundReply(ChatInputInteractionEvent event) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.color(TachiyomiHelper.color())
						.title("No sources found with that id")
						.description("There was no source found with that id. This could be because either it doesn't exists or that it has been removed due to being to hard to maintain or the scanlator asked to be removed.")
						.build());
	}

}
