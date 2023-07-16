package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class BugEventHandler implements SlashCommandEventHandler.SubSlashCommand {

	@Override
	public String getName() {
		return "bug";
	}

	@Component
	public static class AppEventHandler extends BugEventHandler {

		private static final Logger LOGGER = LoggerFactory.getLogger(AppEventHandler.class);

		private final List<TachiyomiProperties.Flavour> flavours;

		@Autowired
		public AppEventHandler(TachiyomiProperties tachiyomiProperties) {
			this.flavours = tachiyomiProperties.getFlavours();
		}

		@Override
		public String getSubName() {
			return "app";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
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
							.build())
					.withComponents(ActionRow.of(
							Button.link("https://github.com/%s/%s/issues".formatted(flavour.getOwner(), flavour.getRepository()), "Search for reported bugs")));
		}

	}

	@Component
	public static class SourceEventHandler extends BugEventHandler {

		@Override
		public String getSubName() {
			return "source";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return event.reply()
					.withEmbeds(EmbedCreateSpec.builder()
							.title("Report source issue for Tachiyomi Extensions")
							.description("""
									To report a bug/issue, click the link below.

									Make sure that you're not reporting something that has already been reported.
									""")
							.build())
					.withComponents(ActionRow.of(
							Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3ABug", "Search for reported bugs")));
		}
	}

	@Component
	public static class WebsiteEventHandler extends BugEventHandler {

		@Override
		public String getSubName() {
			return "website";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return event.reply()
					.withEmbeds(EmbedCreateSpec.builder()
							.title("Report website issue for Tachiyomi Website")
							.description("""
									To report a bug/issue, click the link below.

									Make sure that you're not reporting something that has already been reported.
									""")
							.build())
					.withComponents(ActionRow.of(
							Button.link("https://github.com/tachiyomiorg/website/issues?q=is%3Aissue+label%3ABug", "Search for reported bugs")));
		}
	}

}
