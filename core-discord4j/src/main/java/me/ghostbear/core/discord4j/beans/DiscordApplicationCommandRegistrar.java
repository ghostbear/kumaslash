package me.ghostbear.core.discord4j.beans;

import discord4j.common.JacksonResources;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import jakarta.annotation.PostConstruct;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DiscordApplicationCommandRegistrar {

	private static final Logger LOG = LoggerFactory.getLogger(DiscordApplicationCommandRegistrar.class);

	private final ApplicationContext applicationContext;
	private final GatewayDiscordClient gatewayDiscordClient;

	public DiscordApplicationCommandRegistrar(ApplicationContext applicationContext, GatewayDiscordClient gatewayDiscordClient) {
		this.applicationContext = applicationContext;
		this.gatewayDiscordClient = gatewayDiscordClient;
	}

	private final List<ApplicationCommandRequest> commands = new ArrayList<>();

	public Object processBean(Object bean, String beanName) throws BeansException {
		if (AnnotationUtils.isAnnotationDeclaredLocally(DiscordComponent.class, bean.getClass())) {
			LOG.debug("Class is annotated with DiscordComponent");
 			ReflectionUtils.doWithMethods(bean.getClass(), method -> {
				if (method.isAnnotationPresent(DiscordInteractionProperties.class)) {
					LOG.debug("Method is annotated with DiscordApplicationCommandProperties");
					try {
						doDiscordApplicationCommandProperties(method, bean);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		return bean;
	}


	private void doDiscordApplicationCommandProperties(Method method, Object bean) throws Exception {
		if (method.getParameterCount() == 0) {
			String s = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean))
					.filter(o -> o instanceof String)
					.map(o -> (String) o)
					.orElseThrow();
			final var mapper = JacksonResources.create();
			if (s.endsWith(".json")) {
				var matcher = new PathMatchingResourcePatternResolver();
				var commands = new ArrayList<ApplicationCommandRequest>();
				for (var resource : matcher.getResources(s)) {
					var request = mapper.getObjectMapper()
							.readValue(resource.getInputStream(), ApplicationCommandRequest.class);
					commands.add(request);
				}
				this.commands.addAll(commands);
			} else {
				var request = mapper.getObjectMapper()
						.readValue(s, ApplicationCommandRequest.class);
				commands.add(request);
			}
		}
	}

	@PostConstruct
	public void init() {
		Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(DiscordComponent.class);
		for (String name : beansWithAnnotation.keySet()) {
			processBean(beansWithAnnotation.get(name), name);
		}
		registerApplicationCommands();
	}

	public void registerApplicationCommands() {

		LOG.info("Registering %s Application Commands".formatted(commands.size()));
		final var applicationService = gatewayDiscordClient.getRestClient().getApplicationService();
		final long applicationId = gatewayDiscordClient.getRestClient().getApplicationId().block();
		applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
				.doOnNext(data -> LOG.debug("Successfully registered %s".formatted(data.name())))
				.doOnError(e -> LOG.error("Failed to register global commands", e))
				.subscribe();
	}
}
