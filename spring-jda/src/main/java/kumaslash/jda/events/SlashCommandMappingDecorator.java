/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.util.function.ThrowingConsumer;

public class SlashCommandMappingDecorator implements ThrowingConsumer<GenericEvent> {

	private final String fullCommandName;
	private final ThrowingConsumer<GenericEvent> eventMappingProxy;

	public SlashCommandMappingDecorator(
			String fullCommandName, ThrowingConsumer<GenericEvent> eventMappingProxy) {
		this.fullCommandName = fullCommandName;
		this.eventMappingProxy = eventMappingProxy;
	}

	@Override
	public void acceptWithException(GenericEvent o) throws Exception {
		if (o instanceof SlashCommandInteractionEvent event
				&& event.getFullCommandName().equals(fullCommandName)) {
			eventMappingProxy.acceptWithException(o);
		}
	}
}
