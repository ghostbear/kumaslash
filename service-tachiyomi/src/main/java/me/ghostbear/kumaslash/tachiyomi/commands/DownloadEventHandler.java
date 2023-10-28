package me.ghostbear.kumaslash.tachiyomi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.github.model.Asset;
import me.ghostbear.kumaslash.github.model.Release;
import me.ghostbear.kumaslash.tachiyomi.TachiyomiFlavourService;
import me.ghostbear.kumaslash.tachiyomi.configuration.TachiyomiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@DiscordComponent
public class DownloadEventHandler {

	private final List<TachiyomiProperties.Flavour> flavours;
	private final TachiyomiFlavourService flavourRepository;

	@Autowired
	public DownloadEventHandler(TachiyomiProperties tachiyomiProperties, TachiyomiFlavourService flavourRepository) {
		this.flavours = tachiyomiProperties.getFlavours();
		this.flavourRepository = flavourRepository;
	}

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/download.json");
	}

	@DiscordInteractionHandler(name = "download")
	public Mono<?> onCommandDownload(ChatInputInteractionEvent event) {
		var target = event.getOption("flavour")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();
		var flavour = flavours.stream()
				.filter(f -> f.getRepository().equals(target))
				.findFirst()
				.orElseThrow();
		return event.deferReply()
				.then(flavourRepository.getLatestRelease(flavour.getOwner(), flavour.getRepository()))
				.flatMap(release -> releaseReply(event, flavour, release));
	}

	Mono<Message> releaseReply(ChatInputInteractionEvent event, TachiyomiProperties.Flavour flavour, Release release) {
		Pattern pattern = Pattern.compile(flavour.getPattern());
		return event.createFollowup()
				.withContent("# %s (%s)".formatted(flavour.getName(), release.tagName()))
				.withComponents(ActionRow.of(
						Button.link(
								release.assets().stream()
										.filter(asset -> pattern.matcher(asset.name()).matches())
										.findFirst()
										.map(Asset::browserDownloadUrl)
										.orElseThrow(),
								"Download"),
						Button.link(release.htmlUrl(), "Changelog")));
	}

}
