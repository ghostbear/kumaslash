package me.ghostbear.kumaslash.guild.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import reactor.core.publisher.Mono;

@DiscordComponent
public class UserEventHandler {

	@DiscordInteractionProperties
	public String getName() {
		return "commands/user.json";
	}

	@DiscordInteractionHandler(name = "user.avatar")
	public Mono<Void> onSubcommandAvatar(ChatInputInteractionEvent event) {
		return event.deferReply()
				.then(
						Mono.defer(() -> Mono.just(
								event.getOption("avatar")
										.flatMap(option -> option.getOption("for_guild"))
										.flatMap(ApplicationCommandInteractionOption::getValue)
										.map(ApplicationCommandInteractionOptionValue::asBoolean)
										.orElse(true))))
				.zipWith(
						Mono.defer(() -> event.getOption("avatar")
										.flatMap(option -> option.getOption("user"))
										.flatMap(ApplicationCommandInteractionOption::getValue)
										.map(ApplicationCommandInteractionOptionValue::asUser)
										.orElseThrow())
								.flatMap(user -> user.asMember(event.getInteraction().getGuildId().orElseThrow())))
				.flatMap(forGuildAndMember -> event.createFollowup()
						.withEmbeds(
								EmbedCreateSpec.builder()
										.image((forGuildAndMember.getT1() ? forGuildAndMember.getT2().getEffectiveAvatarUrl() : forGuildAndMember.getT2().getAvatarUrl()) + "?size=512")
										.footer(EmbedCreateFields.Footer.of("Avatar for %s".formatted(forGuildAndMember.getT2().getDisplayName()), null))
										.build()))
				.then();
	}

	@DiscordInteractionHandler(name = "user.banner")
	public Mono<Void> onSubcommandBanner(ChatInputInteractionEvent event) {
		return Mono.defer(() -> event.getOption("banner")
						.flatMap(option -> option.getOption("user"))
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asUser)
						.orElseThrow())
						.flatMap(user -> user.asMember(event.getInteraction().getGuildId().orElseThrow()))
				.filter(member -> member.getBannerUrl().isPresent())
				.flatMap(member -> event.reply()
						.withEmbeds(
								EmbedCreateSpec.builder()
										.image(
												member.getBannerUrl()
														.map(value -> value + "?size=1024")
														.map(Possible::of)
														.orElse(Possible.absent()))
										.footer(EmbedCreateFields.Footer.of("Banner for %s".formatted(member.getDisplayName()), null))
										.build()))
				.switchIfEmpty(Mono.defer(() -> event.reply()
						.withEphemeral(true)
						.withContent("No banner found for user")))
				.then();
	}
}
