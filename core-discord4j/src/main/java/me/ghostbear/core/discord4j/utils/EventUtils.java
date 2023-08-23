package me.ghostbear.core.discord4j.utils;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;

import java.util.Objects;
import java.util.Optional;

public final class EventUtils {

	public static boolean isSubCommandGroup(Event event) {
		if (!(event instanceof ChatInputInteractionEvent e)) {
			return false;
		}

		return e.getOptions().stream()
				.anyMatch(option -> Objects.equals(option.getType(), ApplicationCommandOption.Type.SUB_COMMAND)
									|| Objects.equals(option.getType(), ApplicationCommandOption.Type.SUB_COMMAND_GROUP));
	}

	public static boolean isCommandWithName(String[] args, int index, ApplicationCommandInteractionOption option) {
		if (args.length == index) return true;
		return option.getOption(args[index])
				.filter(o -> isCommandWithName(args, index + 1, o))
				.isPresent();
    }

	public static boolean isCommandWithName(String[] args, int index, ChatInputInteractionEvent event) {
		return event.getOption(args[index])
				.filter(option -> isCommandWithName(args, index + 1, option))
				.isPresent();
    }
}
