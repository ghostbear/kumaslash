package me.ghostbear.kumaslash.guild.controllers;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.DiscordApplicationCommandRequest;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.guild.repositories.RuleRepository;
import me.ghostbear.kumaslash.guild.domain.Rule;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@DiscordComponent
public class RulesController {

	private final RuleRepository ruleRepository;

	public RulesController(RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@DiscordInteractionProperties
	public me.ghostbear.core.discord4j.utils.Publisher commandProperties() {
		return () -> ruleRepository.findAll()
				.groupBy(Rule::guildSnowflake)
				.flatMap(longGuildRuleGroupedFlux -> longGuildRuleGroupedFlux
						.buffer()
						.map(guildRules -> DiscordApplicationCommandRequest.withName("rules")
								.withGuildId(Snowflake.of(longGuildRuleGroupedFlux.key()))
								.withDescription("Rules for %s".formatted(longGuildRuleGroupedFlux.key()))
								.withOptions(List.of(
										ApplicationCommandOptionData.builder()
												.type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
												.name("view")
												.description("Lorem Ipsum")
												.options(List.of(
														ApplicationCommandOptionData.builder()
																.type(ApplicationCommandOption.Type.USER.getValue())
																.name("user")
																.description("The user")
																.build(),
														ApplicationCommandOptionData.builder()
																.type(ApplicationCommandOption.Type.STRING.getValue())
																.name("index")
																.addAllChoices(guildRules.stream()
																		.map(rule -> (ApplicationCommandOptionChoiceData) ApplicationCommandOptionChoiceData.builder()
																				.name(String.valueOf( rule.index()))
																				.value(String.valueOf( rule.index()))
																				.build())
																		.toList())
																.description("The rule to display")
																.build()
												))
												.build(),
										ApplicationCommandOptionData.builder()
												.type(ApplicationCommandOption.Type.SUB_COMMAND_GROUP.getValue())
												.name("edit")
												.description("Lorem Ipsum")
												.options(List.of(
														ApplicationCommandOptionData.builder()
																.type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
																.name("get")
																.description("Lorem Ipsum")
																.build(),
														ApplicationCommandOptionData.builder()
																.type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
																.name("edit")
																.description("Lorem Ipsum")
																.build(),
														ApplicationCommandOptionData.builder()
																.type(ApplicationCommandOption.Type.SUB_COMMAND.getValue())
																.name("delete")
																.description("Lorem Ipsum")
																.build()))
												.build()
								))));
	}

	@DiscordInteractionHandler(name = "rules.view")
	public Publisher<?> handle(ChatInputInteractionEvent event) {
		Snowflake snowflake = event.getInteraction().getGuildId().orElseThrow();
		Optional<Long> index = event.getOption("index")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.map(Long::valueOf);
		Mono<User> user = event.getOption("user")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElse(Mono.empty());
		Flux<Rule> guildRules = getRules(snowflake, index);
		Mono<Possible<String>> mention = user.map(User::getMention)
				.map(Possible::of)
				.switchIfEmpty(Mono.just(Possible.absent()));
		return event.deferReply()
				.thenMany(guildRules)
				.collectList()
				.zipWith(mention)
				.flatMap(tuple -> {
					if (tuple.getT1().size() > 1) {
						return rulesReply(event, tuple.getT1(), tuple.getT2());
					}
					return ruleReply(event,  tuple.getT1().get(0), tuple.getT2());
				});
	}

	Mono<Message> rulesReply(ChatInputInteractionEvent event, List<Rule> rules, Possible<String> mention) {
		return event.createFollowup()
				.withContent(mention)
				.withEmbeds(
						EmbedCreateSpec.builder()
								.color(Color.BISMARK)
								.title("Server Rules")
								.description(rules.stream()
										.map(RulesController.this::getFormatted)
										.collect(Collectors.joining("\n\n")))
								.build());
	}

	Mono<Message> ruleReply(ChatInputInteractionEvent event, Rule rule, Possible<String> mention) {
		return event.createFollowup()
				.withContent(mention)
				.withEmbeds(
						EmbedCreateSpec.builder()
								.color(Color.BISMARK)
								.title("Server Rule")
								.description(getFormatted(rule))
								.footer(EmbedCreateFields.Footer.of("Full list of rules can be found in #rules or by using /rules.", null))
								.build());
	}

	String getFormatted(Rule rule) {
		return "%s. **%s** %s".formatted(rule.index(), rule.title(), rule.description());
	}

	Flux<Rule> getRules(Snowflake snowflake, Optional<Long> index) {
		return Mono.justOrEmpty(index)
				.flatMap(i -> ruleRepository.findByGuildSnowflakeAndIndex(snowflake.asLong(), i.intValue()))
				.flux()
				.switchIfEmpty(Flux.defer(() -> ruleRepository.findByGuildSnowflake(snowflake.asLong())));
	}

}
