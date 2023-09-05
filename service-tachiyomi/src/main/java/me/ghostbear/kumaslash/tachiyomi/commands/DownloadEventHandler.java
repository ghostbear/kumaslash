package me.ghostbear.kumaslash.tachiyomi.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.kumaslash.github.model.Asset;
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
	public String commandProperties() {
		return "commands/download.json";
	}

	@DiscordInteractionHandler(name = "download")
	public Mono<Void> onCommandDownload(ChatInputInteractionEvent event) {
		var target = event.getOption("flavour")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();
		var flavour = flavours.stream()
				.filter(f -> f.getRepository().equals(target))
				.findFirst()
				.orElseThrow();
		Pattern pattern = Pattern.compile(flavour.getPattern());
		return event.deferReply()
				.then(flavourRepository.getLatestRelease(flavour.getOwner(), flavour.getRepository()))
				.flatMap(release -> event.createFollowup()
						.withContent("# %s (%s)".formatted(flavour.getName(), release.tagName()))
						.withComponents(ActionRow.of(
								Button.link(
										release.assets().stream()
												.filter(asset -> pattern.matcher(asset.name()).matches())
												.findFirst()
												.map(Asset::browserDownloadUrl)
												.orElseThrow(),
										"Download"),
								Button.link(release.htmlUrl(), "Changelog")
						)))
				.then();
	}
}
