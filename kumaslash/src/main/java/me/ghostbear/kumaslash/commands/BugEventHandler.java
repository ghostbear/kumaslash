package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import reactor.core.publisher.Mono;

import java.util.List;

@DiscordComponent
public class BugEventHandler {

	private final List<TachiyomiProperties.Flavour> flavours;

	public BugEventHandler(TachiyomiProperties tachiyomiProperties) {
		this.flavours = tachiyomiProperties.getFlavours();
	}

	@DiscordInteractionProperties
	public String commandProperties() {
		return "commands/bug.json";
	}

	@DiscordInteractionHandler(name = "bug.app")
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
						.title("Report issue for %s".formatted(flavour.getName()))
						.description("""
									To report a bug/issue, click the link below.

									Make sure that you're not reporting something that has already been reported.
									""")
						.thumbnail(Possible.of(flavour.getIconUrl()))
						.build())
				.withComponents(ActionRow.of(
						Button.link("https://github.com/%s/%s/issues".formatted(flavour.getOwner(), flavour.getRepository()), "Search for reported bugs")));
	}

	@DiscordInteractionHandler(name = "bug.source")
	public Mono<Void> onSubcommandSource(ChatInputInteractionEvent event) {
		return event.reply()
				.withEmbeds(EmbedCreateSpec.builder()
						.title("Report source issue for Tachiyomi Extensions")
						.description("""
									To report a bug/issue, click the link below.

									Make sure that you're not reporting something that has already been reported.
									""")
						.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
						.build())
				.withComponents(ActionRow.of(
						Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3ABug", "Search for reported bugs")));
	}

	@DiscordInteractionHandler(name = "bug.website")
	public Mono<Void> onSubcommandWebsite(ChatInputInteractionEvent event) {
		return event.reply()
				.withEmbeds(EmbedCreateSpec.builder()
						.title("Report website issue for Tachiyomi Website")
						.description("""
									To report a bug/issue, click the link below.

									Make sure that you're not reporting something that has already been reported.
									""")
						.thumbnail("https://raw.githubusercontent.com/tachiyomiorg/tachiyomi/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png")
						.build())
				.withComponents(ActionRow.of(
						Button.link("https://github.com/tachiyomiorg/website/issues?q=is%3Aissue+label%3ABug", "Search for reported bugs")));
	}

}
