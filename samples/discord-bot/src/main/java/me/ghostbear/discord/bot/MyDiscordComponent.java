package me.ghostbear.discord.bot;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.*;
import discord4j.core.object.entity.User;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@DiscordComponent
public class MyDiscordComponent {

	private static final Logger LOG = LoggerFactory.getLogger(MyDiscordComponent.class);


	@DiscordInteractionProperties
	public String applicationCommandRaw() {
		// language=json
		return """
				{
				  "name": "test",
				  "description": "This is a test command",
				  "options": [
				    {
				      "type": 2,
					  "name": "x",
					  "description": "This is a test command",
					  "options": [
						{
						 "type": 1,
						 "name": "a",
						 "description": "Test command A",
						 "options": [
						   {
							 "type": 6,
							 "name": "user",
							 "description": "The user whose avatar to get",
							 "required": true
						   }
						 ]
						},
						{
						 "type": 1,
						 "name": "b",
						 "description": "Test command B",
						 "options": [
						   {
							 "type": 6,
							 "name": "user",
							 "description": "The user whose banner to get",
							 "required": true
						   }
						 ]
						},
						{
						 "type": 1,
						 "name": "c",
						 "description": "Test command C"
						},
						{
						 "type": 1,
						 "name": "d",
						 "description": "Test command D"
						}
					  ]
					}
				  ]
				}
				""";
	}

	@DiscordInteractionHandler(name = "test.x.a")
	public Publisher<?> onSubcommandA(ChatInputInteractionEvent event) {
		return event.getOption("x")
				.flatMap(option -> option.getOption("a"))
				.flatMap(option -> option.getOption("user"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElse(Mono.empty())
				.map(User::getId)
				.flatMap(snowflake -> event.reply("Hello, %s".formatted(snowflake)));
	}

	@DiscordInteractionHandler(name = "test.x.b")
	public Mono<?> onSubcommandB(ChatInputInteractionEvent event) {
		return event.getOption("x")
				.flatMap(option -> option.getOption("b"))
				.flatMap(option -> option.getOption("user"))
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asUser)
				.orElse(Mono.empty())
				.map(User::getId)
				.flatMap(snowflake -> event.reply("Hi, %s".formatted(snowflake)));
	}

	@DiscordInteractionHandler(name = "test.x.c")
	public Mono<?> onSubcommandC(ChatInputInteractionEvent event) {
		var actionComponents = new ActionComponent[] {
				TextInput.small("the_cake_is_a_lie", "What is your name?")
						.placeholder("Cake")
		};

		return event.presentModal(
				"My Test Modal",
				"testModal",
				Arrays.stream(actionComponents)
						.map(ActionRow::of)
						.collect(Collectors.toList()));
	}

	@DiscordInteractionHandler(name = "test.x.d")
	public Mono<?> onSubcommandD(ChatInputInteractionEvent event) {
		return event.reply("Hey, listen")
				.withComponents(ActionRow.of(Button.primary("testButton", "Listen")));
	}

	@DiscordInteractionHandler(name = "testModal")
	public Mono<?> onModal(ModalSubmitInteractionEvent event) {
		return event.reply(
				"Hi, %s".formatted(
						event.getComponents(TextInput.class).stream()
								.filter(i -> i.getCustomId().equals("the_cake_is_a_lie"))
								.findFirst()
								.flatMap(TextInput::getValue)
								.orElse("Unknown")));
	}

	@DiscordInteractionHandler(name = "testOtherModal")
	public Mono<?> onOtherModal(ModalSubmitInteractionEvent event) {
		return event.reply("Hi, the cake was a lie");
	}

	@DiscordInteractionHandler(name = "testButton")
	public Mono<?> onButton(ButtonInteractionEvent event) {
		return event.reply("Hi, from Button");
	}

	@DiscordEventHandler
	public Publisher<?> onMessageCreated(MessageCreateEvent messageCreateEvent) {
		return messageCreateEvent.getMessage()
				.getChannel()
				.filter(ignored -> messageCreateEvent.getMessage().getContent().equals("ping"))
				.flatMap(messageChannel -> messageChannel.createMessage("Pong!"));
	}

	@DiscordEventHandler
	public Publisher<?> onGuildCreate(GuildCreateEvent guildCreateEvent) {
		LOG.info("Guild {} just joined to club", guildCreateEvent.getGuild().getId());
		return Mono.empty();
	}

}
