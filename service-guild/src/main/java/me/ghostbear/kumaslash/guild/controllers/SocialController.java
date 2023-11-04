package me.ghostbear.kumaslash.guild.controllers;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.repositories.SocialRepository;
import me.ghostbear.kumaslash.guild.domain.Social;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@DiscordComponent
public class SocialController {
	private final SocialRepository socialRepository;

	@Autowired
	public SocialController(SocialRepository socialRepository) {
		this.socialRepository = socialRepository;
	}

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/social.json");
	}

	@DiscordInteractionHandler(name = "social")
	public Mono<?> handle(ChatInputInteractionEvent event) {
		var interaction = event.getInteraction();
		var interactor = interaction.getUser();
		var guildId = interaction.getGuildId().orElseThrow();
		var targetUser = event.getOption("user")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElseThrow();
		var action = event.getOption("action")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.orElseThrow();

		return targetUser.filter(user -> !user.getId().equals(interactor.getId()))
				.flatMap(user -> event.deferReply().thenReturn(user))
				.flatMap(user -> user.asMember(guildId))
				.zipWith(Mono.defer(() -> interactor.asMember(guildId)))
				.zipWith(
						Mono.defer(() -> socialRepository.findByGuildSnowflakeAndAction(guildId.asLong(), Social.Action.valueOf(action))),
						(tuple, social) -> Tuples.of(tuple.getT2(), tuple.getT1(), social))
				.flatMap(tuple -> successReply(event, tuple.getT1(), tuple.getT2(), tuple.getT3()))
				.switchIfEmpty(Mono.defer(() -> targetSelfReply(event)));
	}

	Mono<Message> successReply(ChatInputInteractionEvent event, Member interactor, Member target, Social social) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.color(Color.PINK)
						.description(interactor.getDisplayName() + " " + social.action().name().toLowerCase() + " " + target.getDisplayName())
						.image(social.imageUrl())
						.build());
	}

	Mono<Message> targetSelfReply(ChatInputInteractionEvent event) {
		return event.deferReply()
				.withEphemeral(true)
				.then(event.createFollowup()
						.withContent("You need to mention someone other than yourself to do this!"));
	}
}
