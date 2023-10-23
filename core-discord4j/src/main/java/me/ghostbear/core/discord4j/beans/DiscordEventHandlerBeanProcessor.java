package me.ghostbear.core.discord4j.beans;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordEventHandler;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionHandler;
import me.ghostbear.core.discord4j.utils.EventUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Optional;

public class DiscordEventHandlerBeanProcessor implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(DiscordEventHandlerBeanProcessor.class);

	private final EventDispatcher eventDispatcher;

	@Autowired
	public DiscordEventHandlerBeanProcessor(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (AnnotationUtils.isAnnotationDeclaredLocally(DiscordComponent.class, bean.getClass())) {
			LOG.debug("Class is annotated with DiscordComponent: {}", bean.getClass().getSimpleName());
			ReflectionUtils.doWithMethods(bean.getClass(), method -> {
				if (method.isAnnotationPresent(DiscordEventHandler.class)) {
					LOG.debug("Method is annotated with DiscordEventHandler");
					doDiscordEventHandler(method, bean);
				} else if (method.isAnnotationPresent(DiscordInteractionHandler.class)) {
					LOG.debug("Method is annotated with DiscordApplicationCommand");
					String name = method.getAnnotation(DiscordInteractionHandler.class).name();
					doDiscordApplicationCommand(method, bean, name);
				}
			});
		}

		return bean;
	}

	private void doDiscordApplicationCommand(Method method, Object bean, String name) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			if (ApplicationCommandInteractionEvent.class.isAssignableFrom(parameterTypes[0])) {
				doApplicationCommandInteractionEvent(name, method, bean, (Class<? extends ApplicationCommandInteractionEvent>) parameterTypes[0]);
			} else if (ComponentInteractionEvent.class.isAssignableFrom(parameterTypes[0])) {
				doComponentInteractionEvent(name, method, bean, (Class<? extends ComponentInteractionEvent>) parameterTypes[0]);
			}
		}
	}

	private void doComponentInteractionEvent(String customId, Method method, Object bean, Class<? extends ComponentInteractionEvent> clazz) {
		eventDispatcher.on(clazz)
				.flatMap(e -> {
                    if (!e.getCustomId().equals(customId)) {
                        return Flux.empty();
                    }
                    return invokeMethod(method, bean, e);
                })
				.onErrorContinue((throwable, object) -> LOG.error("Component Interaction Event Handler failed spectacularly", throwable))
				.subscribe();
	}

	private void doApplicationCommandInteractionEvent(String commandName, Method method, Object bean, Class<? extends ApplicationCommandInteractionEvent> clazz) {
		String[] args = commandName.split("\\.");
		eventDispatcher.on(clazz)
				.flatMap(e -> {
					if (e instanceof ChatInputInteractionEvent event) {
                        return doChatInputInteractionEvent(args, method, bean, event);
                    }
                    if (e.getCommandName().equals(commandName)) {
                        return invokeMethod(method, bean, e);
                    }
                    return Flux.error(new RuntimeException("%s not supported".formatted(e.getClass().getName())));
				})
				.doOnCancel(() -> LOG.debug("{} was cancelled", commandName))
				.onErrorContinue((throwable, object) -> LOG.error("Application Command Event Handler failed spectacularly", throwable))
				.subscribe();
	}

	private Publisher<?> doChatInputInteractionEvent(String[] args, Method method, Object bean, ChatInputInteractionEvent event) {
		if (!event.getCommandName().equals(args[0])) {
			return Flux.empty();
		}
		LOG.debug("{}", args[0]);
		if (EventUtils.isSubCommandGroup(event) && args.length <= 1) {
			LOG.error("Event is sub command: {}, Command length is {}", EventUtils.isSubCommandGroup(event), args.length);
			return Flux.error(new RuntimeException("Expected command name to be a subcommand, i.e. \"myCommand.mySubCommand\""));
		}
		if (!EventUtils.isSubCommandGroup(event)) {
			return invokeMethod(method, bean, event);
		}

		return EventUtils.isCommandWithName(args, 1, event) ? invokeMethod(method, bean, event) : Flux.empty();
	}

	private void doDiscordEventHandler(Method method, Object bean) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			if (Event.class.isAssignableFrom(parameterTypes[0])) {
				doEvent(method, bean, (Class<? extends Event>) parameterTypes[0]);
			}
		}
	}

	private void doEvent(Method method, Object bean, Class<? extends Event> clazz) {
		eventDispatcher.on(clazz)
				.flatMap(e -> invokeMethod(method, bean, e))
				.onErrorContinue((throwable, object) -> LOG.error("Event Handler failed spectacularly", throwable))
				.subscribe();
	}

	public <T extends Event> Publisher<?> invokeMethod(Method method, Object bean, T event) {
		return Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean, event))
				.filter(o -> o instanceof Publisher<?>)
				.map(o -> (Publisher<?>) o)
				.orElse(Mono.empty());
	}

}
