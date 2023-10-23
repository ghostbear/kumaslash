package me.ghostbear.kumaslash.tachiyomi.commands;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.core.discord4j.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import me.ghostbear.kumaslash.tachiyomi.util.ComponentHelper;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@DiscordComponent
public class HelpSlashCommandEventHandler {

	private static final String MODAL_ID = "help_slash_command";

	@DiscordInteractionProperties
	public Resources commandProperties() {
		return Resources.of("commands/help.json", "commands/help_context.json");
	}

	@DiscordInteractionHandler(name = "help")
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		var actionComponents = actionComponents();
		return event.presentModal()
				.withCustomId(MODAL_ID)
				.withTitle("Answer the following questions")
				.withComponents(
						Arrays.stream(actionComponents)
								.map(ActionRow::of)
								.collect(Collectors.toList()));
	}

	@DiscordInteractionHandler(name = "Help")
	public Publisher<?> onMessageInteraction(MessageInteractionEvent event) {
		return event.deferReply()
				.then(event.createFollowup()
						.withContent("%s, please follow the Troubleshooting link below, if that doesn't solve your issues, please click the Help button.".formatted(event.getResolvedMessage().getAuthor().map(User::getMention).orElse("Unknown user")))
						.withComponents(ActionRow.of(
								Button.primary("help_button", "Help"),
								Button.link("https://tachiyomi.org/help/guides/troubleshooting/", "Troubleshooting")
						)));
	}

	@DiscordInteractionHandler(name = "help_button")
	public Publisher<?> onButtonInteraction(ButtonInteractionEvent event) {
		var actionComponents = actionComponents();
		return event.presentModal()
				.withCustomId(MODAL_ID)
				.withTitle("Answer the following questions")
				.withComponents(
						Arrays.stream(actionComponents)
								.map(ActionRow::of)
								.collect(Collectors.toList()));
	}

	@DiscordInteractionHandler(name = MODAL_ID)
	public Mono<Void> onModal(ModalSubmitInteractionEvent event) {
		var member = event.getInteraction().getMember();
		var actionComponents = actionComponents();
		return event.reply()
				.withEmbeds(
						EmbedCreateSpec.builder()
								.color(Color.of(47, 49, 54))
								.addField(
										EmbedCreateFields.Field.of(
												"What version of the app are you on?",
												ComponentHelper.textInput(event, actionComponents[0]).flatMap(TextInput::getValue).orElseThrow(),
												false))
								.addField(
										EmbedCreateFields.Field.of(
												"What source are you having issues with?",
												ComponentHelper.textInput(event, actionComponents[1]).flatMap(TextInput::getValue).orElseThrow(),
												false))
								.addField(
										EmbedCreateFields.Field.of(
												"What device are you using?",
												ComponentHelper.textInput(event, actionComponents[2]).flatMap(TextInput::getValue).orElseThrow(),
												false))
								.addField(
										EmbedCreateFields.Field.of(
												"What Android version are you on?",
												ComponentHelper.textInput(event, actionComponents[3]).flatMap(TextInput::getValue).orElseThrow(),
												false))
								.addField(
										EmbedCreateFields.Field.of(
												"What issue are you having?",
												ComponentHelper.textInput(event, actionComponents[4]).flatMap(TextInput::getValue).orElseThrow(),
												false))
								.footer(
										EmbedCreateFields.Footer.of(
												"Answered by %s".formatted(member.map(Member::getDisplayName).orElseThrow()),
												member.map(Member::getEffectiveAvatarUrl).orElseThrow()))
								.build());
	}

	ActionComponent[] actionComponents() {
		return new ActionComponent[] {
				TextInput.small("first", "What version of the app are you on?")
						.placeholder("Tachiyomi 1.13.3"),
				TextInput.small("second", "What source are you having issues with?")
						.placeholder("Example: MangaDex 1.2.158"),
				TextInput.small("third", "What device are you using?")
						.placeholder("Example: Google Pixel 6"),
				TextInput.small("fourth", "What Android version are you on?")
						.placeholder("Example: Android 12L"),
				TextInput.paragraph("fifth", "What issue are you having?", 10, 500)
						.placeholder("Please explain your issue here in detail and include the error if there is any")
		};
	}

}
