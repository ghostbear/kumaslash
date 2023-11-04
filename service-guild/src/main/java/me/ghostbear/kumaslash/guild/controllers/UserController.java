package me.ghostbear.kumaslash.guild.controllers;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import reactor.core.publisher.Mono;

import java.util.Optional;

@DiscordComponent
public class UserController {

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/user.json");
	}

	@DiscordInteractionHandler(name = "user.avatar")
	public Mono<?> onSubcommandAvatar(ChatInputInteractionEvent event) {
		Optional<ApplicationCommandInteractionOption> option = event.getOption("avatar");
		Boolean forGuild = option
				.flatMap(o -> o.getOption("for_guild"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asBoolean)
				.orElse(true);
		Mono<User> user = option
				.flatMap(o -> o.getOption("user"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElseThrow();
		return event.deferReply()
				.then(Mono.defer(() -> Mono.just(forGuild)))
				.zipWith(user.flatMap(u -> u.asMember(event.getInteraction().getGuildId().orElseThrow())))
				.flatMap(tuple -> avatarReply(event, tuple.getT1(), tuple.getT2()));
	}

	Mono<Message> avatarReply(ChatInputInteractionEvent event, boolean forGuild, Member user) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.image((forGuild ? user.getEffectiveAvatarUrl() : user.getAvatarUrl()) + "?size=512")
						.footer(EmbedCreateFields.Footer.of("Avatar for %s".formatted(user.getDisplayName()), null))
						.build());
	}

	@DiscordInteractionHandler(name = "user.banner")
	public Mono<Void> onSubcommandBanner(ChatInputInteractionEvent event) {
		Mono<User> user = event.getOption("banner")
				.flatMap(option -> option.getOption("user"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElseThrow();
		return event.deferReply()
				.then(user)
				.flatMap(u -> u.asMember(event.getInteraction().getGuildId().orElseThrow()))
				.filter(member -> member.getBannerUrl().isPresent())
				.flatMap(member -> bannerReply(event, member))
				.switchIfEmpty(Mono.defer(() -> bannerNotFoundReply(event)))
				.then();
	}

	Mono<Message> bannerReply(ChatInputInteractionEvent event, Member member) {
		return event.createFollowup()
				.withEmbeds(EmbedCreateSpec.builder()
						.image(member.getBannerUrl()
								.map(value -> value + "?size=1024")
								.map(Possible::of)
								.orElse(Possible.absent()))
						.footer(EmbedCreateFields.Footer.of("Banner for %s".formatted(member.getDisplayName()), null))
						.build());
	}

	Mono<Message> bannerNotFoundReply(ChatInputInteractionEvent event) {
		return event.createFollowup()
				.withEphemeral(true)
				.withContent("No banner found for user");
	}

}
