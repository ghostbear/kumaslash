package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import me.ghostbear.kumaslash.data.tachiyomi.TachiyomiExtensionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SourceEventHandler implements SlashCommandEventHandler.SlashCommand {

	private final TachiyomiExtensionService tachiyomiExtensionService;

	@Autowired
	public SourceEventHandler(TachiyomiExtensionService tachiyomiExtensionService) {
		this.tachiyomiExtensionService = tachiyomiExtensionService;
	}

	@Override
	public String getName() {
		return "source";
	}

	@Override
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
						.withContent("No source found with that id")))
				.onErrorResume(throwable -> event.createFollowup()
						.withContent(throwable.getMessage()))
				.then();
	}
}
