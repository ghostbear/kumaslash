package me.ghostbear.kumaslash.util;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.ActionComponent;
import discord4j.core.object.component.TextInput;
import java.util.Optional;

public class ComponentHelper {

	private ComponentHelper() {
	}

	public static Optional<TextInput> textInput(ModalSubmitInteractionEvent event, String customId) {
		return event.getComponents(TextInput.class)
				.stream()
				.filter(textInput -> textInput.getCustomId().equals(customId))
				.findFirst();
	}

	public static Optional<TextInput> textInput(ModalSubmitInteractionEvent event, ActionComponent textInput) {
		return textInput(event, textInput.getData().customId().toOptional().orElseThrow());
	}

}
