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

public abstract class FeatureEventHandler implements SlashCommandEventHandler.SubSlashCommand {

	@Override
	public String getName() {
		return "feature";
	}

	@Component
	public static class AppEventHandler extends FeatureEventHandler {

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
							.title("Request feature for %s".formatted(flavour.getName()))
							.description("""
									To request a feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
							.build())
					.withComponents(ActionRow.of(Button.link("https://github.com/%s/%s/issues".formatted(flavour.getOwner(), flavour.getRepository()), "Search for requested features")));
		}

	}

	@Component
	public static class SourceEventHandler extends FeatureEventHandler {

		@Override
		public String getSubName() {
			return "source";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return event.reply()
					.withEmbeds(EmbedCreateSpec.builder()
							.title("Request new source/feature for Tachiyomi Extensions")
							.description("""
									To request a source, or a source feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
							.build())
					.withComponents(ActionRow.of(
							Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3A\"Feature+request\"", "Search for requested features"),
							Button.link("https://github.com/tachiyomiorg/tachiyomi-extensions/issues?q=is%3Aissue+label%3A\"Source+request\"", "Search for requested sources")));
		}
	}

	@Component
	public static class WebsiteEventHandler extends FeatureEventHandler {

		@Override
		public String getSubName() {
			return "website";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return event.reply()
					.withEmbeds(EmbedCreateSpec.builder()
							.title("Request website feature for Tachiyomi Website")
							.description("""
									To request a feature, click the link below.

									Make sure that you're not requesting something that has already been requested.
									""")
							.build())
					.withComponents(ActionRow.of(
							Button.link("https://github.com/tachiyomiorg/website/issues", "Search for requested features")));
		}
	}

}
