package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import me.ghostbear.kumaslash.data.github.Asset;
import me.ghostbear.kumaslash.data.tachiyomi.TachiyomiFlavourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class DownloadEventHandler implements SlashCommandEventHandler.SlashCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadEventHandler.class);

	private final List<TachiyomiProperties.Flavour> flavours;
	private final TachiyomiFlavourService flavourRepository;

	@Autowired
	public DownloadEventHandler(TachiyomiProperties tachiyomiProperties, TachiyomiFlavourService flavourRepository) {
		this.flavours = tachiyomiProperties.getFlavours();
		this.flavourRepository = flavourRepository;
	}

	@Override
	public String getName() {
		return "download";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
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
