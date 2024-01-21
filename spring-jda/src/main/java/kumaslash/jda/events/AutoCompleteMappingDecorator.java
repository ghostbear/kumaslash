/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import org.springframework.util.function.ThrowingConsumer;

import java.util.regex.Pattern;

public class AutoCompleteMappingDecorator implements ThrowingConsumer<GenericEvent> {
	private final Pattern pattern;
	private final ThrowingConsumer<GenericEvent> eventMappingProxy;

	public AutoCompleteMappingDecorator(
			String value, ThrowingConsumer<GenericEvent> eventMappingProxy) {
		this(Pattern.compile(value), eventMappingProxy);
	}

	public AutoCompleteMappingDecorator(
			Pattern pattern, ThrowingConsumer<GenericEvent> eventMappingProxy) {
		this.pattern = pattern;
		this.eventMappingProxy = eventMappingProxy;
	}

	@Override
	public void acceptWithException(GenericEvent o) throws Exception {
		if (o instanceof CommandAutoCompleteInteractionEvent event
				&& pattern.matcher(event.getFullCommandName()).matches()) {
			eventMappingProxy.acceptWithException(o);
		}
	}
}
