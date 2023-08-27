package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.kumaslash.data.guild.GuildSocial;
import me.ghostbear.kumaslash.data.guild.GuildSocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@DiscordComponent
public class SocialEventHandler {

	private final GuildSocialRepository socialRepository;

	@Autowired
	public SocialEventHandler(GuildSocialRepository socialRepository) {
		this.socialRepository = socialRepository;
	}

	@DiscordInteractionProperties
	public String applicationProperties() {
		return "commands/social.json";
	}

	@DiscordInteractionHandler(name = "social")
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		var targetSnowflake = event.getOption("user")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asSnowflake)
				.orElseThrow();
		var interactorSnowflake = event.getInteraction().getUser().getId();
		if (targetSnowflake.equals(interactorSnowflake)) {
			return event.reply()
					.withEphemeral(true)
					.withContent("You need to mention someone other than yourself to do this!")
					.then();
		}

		return event.deferReply()
				.then(Mono.defer(() -> event.getOption("user")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asUser)
						.orElseThrow()
						.flatMap(user -> user.asMember(event.getInteraction().getGuildId().orElseThrow()))))
				.zipWith(Mono.defer(() -> event.getInteraction().getUser().asMember(event.getInteraction().getGuildId().orElseThrow())))
				.zipWith(Mono.defer(() -> Mono.just(
								event.getOption("action")
										.flatMap(ApplicationCommandInteractionOption::getValue)
										.map(ApplicationCommandInteractionOptionValue::asString)
										.orElseThrow())
						.flatMap(action -> socialRepository.findByGuildSnowflakeAndAction(event.getInteraction().getGuildId().orElseThrow().asLong(), GuildSocial.Action.valueOf(action)))))
				.flatMap(value -> event.createFollowup()
						.withEmbeds(EmbedCreateSpec.builder()
								.color(Color.PINK)
								.description(value.getT1().getT2().getDisplayName() + " " + value.getT2().action().name().toLowerCase() + " " + value.getT1().getT1().getDisplayName())
								.image(value.getT2().imageUrl())
								.build()))
				.then();
	}
}
