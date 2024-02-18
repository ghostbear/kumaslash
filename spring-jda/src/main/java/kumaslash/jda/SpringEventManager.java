/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda;

import kumaslash.jda.annotations.AutoCompleteMapping;
import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.AutoCompleteMappingDecorator;
import kumaslash.jda.events.EventMappingProxy;
import kumaslash.jda.events.SlashCommandMappingDecorator;
import kumaslash.jda.utils.JDAUtils;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.ThrowingConsumer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.springframework.util.ClassUtils.CGLIB_CLASS_SEPARATOR;

@Component
public class SpringEventManager implements IEventManager {

	private static final Logger LOG = LoggerFactory.getLogger(SpringEventManager.class);

	private final Set<Object> registeredListeners = ConcurrentHashMap.newKeySet();
	private final Map<Class<? extends GenericEvent>, List<Consumer<GenericEvent>>> methods =
			new ConcurrentHashMap<>();

	@Override
	public void register(Object o) {

		if (o.getClass().getName().contains(CGLIB_CLASS_SEPARATOR) &&
			o.getClass().getSuperclass().getAnnotation(JDAController.class) == null) {
			throw new IllegalArgumentException("CGLIB Class, %s, must be annotated with JDAController".formatted(o.getClass().getName()));
		} else if (!o.getClass().getName().contains(CGLIB_CLASS_SEPARATOR) && !AnnotationUtils.isAnnotationDeclaredLocally(JDAController.class, o.getClass())) {
			throw new IllegalArgumentException("Class, %s, must be annotated with JDAController".formatted(o.getClass().getName()));
		}
		registeredListeners.add(o);

		ReflectionUtils.doWithMethods(o.getClass(), method -> {
			if (!JDAUtils.isEventMapping(method)) {
				return;
			}
			Class<? extends GenericEvent> parameterType =
					(Class<? extends GenericEvent>) method.getParameterTypes()[0];
			Consumer<GenericEvent> consumer = getGenericEventConsumer(o, method, parameterType);
			methods.compute(parameterType, (aClass, consumers) -> {
				if (consumers == null) consumers = new ArrayList<>();
				consumers.add(consumer);
				return consumers;
			});
		});
	}

	private Consumer<GenericEvent> getGenericEventConsumer(
			Object o, Method method, Class<? extends GenericEvent> parameterType) {
		if (method.getReturnType() != Void.TYPE) {
			throw new IllegalStateException(
					"Method (%s) must return void".formatted(method.getName()));
		}
		if (method.getParameterCount() != 1
				|| !GenericEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
			throw new IllegalStateException(
					"Method (%s) must have one argument, which must be assignable from type of GenericEvent"
							.formatted(method.getName()));
		}
		if (method.isAnnotationPresent(SlashCommandMapping.class)
				&& parameterType != SlashCommandInteractionEvent.class) {
			throw new IllegalStateException(
					"Method (%s) annotated with SlashCommandMapping must have one argument, which must be of type SlashCommandInteractionEvent"
							.formatted(method.getName()));
		}

		return getGenericEventThrowingConsumer(o, method);
	}

	private static ThrowingConsumer<GenericEvent> getGenericEventThrowingConsumer(
			Object o, Method method) {
		ThrowingConsumer<GenericEvent> eventMappingProxy = new EventMappingProxy(o, method);
		if (method.isAnnotationPresent(SlashCommandMapping.class)) {
			SlashCommandMapping slashCommandMapping =
					method.getAnnotation(SlashCommandMapping.class);
			eventMappingProxy =
					new SlashCommandMappingDecorator(slashCommandMapping.name(), eventMappingProxy);
		} else if (method.isAnnotationPresent(AutoCompleteMapping.class)) {
			AutoCompleteMapping autoCompleteMapping =
					method.getAnnotation(AutoCompleteMapping.class);
			eventMappingProxy = new AutoCompleteMappingDecorator(
					autoCompleteMapping.value(), eventMappingProxy);
		}
		return eventMappingProxy;
	}

	@Override
	public void unregister(Object o) {
		LOG.debug("Unregistered: {}", o);
	}

	@Override
	public void handle(GenericEvent genericEvent) {
		Class<? extends GenericEvent> eventClass = genericEvent.getClass();
		if (!methods.containsKey(eventClass)) {
			return;
		}
		for (Consumer<GenericEvent> consumer :
				methods.getOrDefault(eventClass, Collections.emptyList())) {
			if (!Objects.nonNull(consumer)) {
				continue;
			}
			consumer.accept(genericEvent);
		}
	}

	@Override
	public List<Object> getRegisteredListeners() {
		return List.copyOf(registeredListeners);
	}
}
