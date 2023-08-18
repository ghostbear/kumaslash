package me.ghostbear.kumaslash.commands.help;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ghostbear.kumaslash.commands.core.ModalEventHandler;
import me.ghostbear.kumaslash.util.ComponentHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class HelpModalEventHandler implements ModalEventHandler {

	public static final String CUSTOM_ID = "help_slash_command";

	@Override
	public String getCustomId() {
		return CUSTOM_ID;
	}

	@Override
	public Mono<Void> handle(ModalSubmitInteractionEvent event) {
		var member = event.getInteraction().getMember();
		var actionComponents = new HelpActionComponentFactory().create();
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
}
