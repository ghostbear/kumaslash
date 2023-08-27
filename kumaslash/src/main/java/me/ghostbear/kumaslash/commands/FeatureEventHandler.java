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
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import reactor.core.publisher.Mono;

import java.util.List;

@DiscordComponent
public class FeatureEventHandler {

	private final List<TachiyomiProperties.Flavour> flavours;

	public FeatureEventHandler(TachiyomiProperties tachiyomiProperties) {
		flavours = tachiyomiProperties.getFlavours();
	}

	@DiscordInteractionProperties
	public String commandProperties() {
		return "commands/feature.json";
	}

	@DiscordInteractionHandler(name = "feature.app")
	public Mono<Void> onSubcommandApp(ChatInputInteractionEvent event) {
		var target = event.getOption("app")
				.flatMap(app -> app.getOption("flavour"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();
		var flavour = flavours.stream()
				.filter(f -> f.getRepository().equalsIgnoreCase(target))
				.findFirst()
				.orElseThrow();
		return event.reply()
				.withEmbeds(EmbedCreateSpec.builder()
						.title("Request feature for %s".formatted(flavour.getName()))
						.description("""
									To request a feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
						.thumbnail(flavour.getIconUrl())
						.build())
				.withComponents(ActionRow.of(Button.link("https://github.com/%s/%s/issues".formatted(flavour.getOwner(), flavour.getRepository()), "Search for requested features")));
	}

	@DiscordInteractionHandler(name = "feature.source")
	public Mono<Void> onSubcommandSource(ChatInputInteractionEvent event) {
		return event.reply()
				.withEmbeds(EmbedCreateSpec.builder()
						.title("Request new source/feature for Tachiyomi Extensions")
						.description("""
									To request a source, or a source feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
						.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
						.build())
				.withComponents(ActionRow.of(
						Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3A\"Feature+request\"", "Search for requested features"),
						Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3A\"Source+request\"", "Search for requested sources")));
	}

	@DiscordInteractionHandler(name = "feature.website")
	public Mono<Void> onSubcommandWebsite(ChatInputInteractionEvent event) {
		return event.reply()
				.withEmbeds(EmbedCreateSpec.builder()
						.title("Request website feature for Tachiyomi Website")
						.description("""
									To request a feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
						.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
						.build())
				.withComponents(ActionRow.of(
						Button.link("https://github.com/tachiyomiorg/website/issues", "Search for requested features")));
	}

}
