/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.events;

import java.lang.reflect.Method;
import net.dv8tion.jda.api.events.GenericEvent;
import org.springframework.util.function.ThrowingConsumer;

public class EventMappingProxy implements ThrowingConsumer<GenericEvent> {

	private final Object instance;
	private final Method method;

	public EventMappingProxy(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

	@Override
	public void acceptWithException(GenericEvent o) throws Exception {
		method.invoke(instance, o);
	}
}
