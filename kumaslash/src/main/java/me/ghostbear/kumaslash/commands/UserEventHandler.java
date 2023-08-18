package me.ghostbear.kumaslash.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.possible.Possible;
import me.ghostbear.kumaslash.commands.core.SlashCommandEventHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

public abstract class UserEventHandler implements SlashCommandEventHandler.SubSlashCommand {

	@Override
	public String getName() {
		return "user";
	}

	@Component
	public static class AvatarEventHandler extends UserEventHandler {

		@Override
		public String getSubName() {
			return "avatar";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return event.deferReply()
					.then(
							Mono.defer(() -> Mono.just(
									option.getOption("for_guild")
											.flatMap(ApplicationCommandInteractionOption::getValue)
											.map(ApplicationCommandInteractionOptionValue::asBoolean)
											.orElse(true))))
					.zipWith(
							Mono.defer(() -> option.getOption("user")
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
	}

	@Component
	public static class BannerEventHandler extends UserEventHandler {

		@Override
		public String getSubName() {
			return "banner";
		}

		@Override
		public Mono<Void> handle(ChatInputInteractionEvent event, ApplicationCommandInteractionOption option) {
			return Mono.defer(() -> option.getOption("user")
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
}
