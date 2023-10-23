package me.ghostbear.core.discord4j.beans;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import jakarta.annotation.PostConstruct;
import me.ghostbear.core.discord4j.DiscordInteractionPropertySupplier;
import me.ghostbear.core.discord4j.Raw;
import me.ghostbear.core.discord4j.Resources;
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
	private final RestClient restClient;

	public DiscordApplicationCommandRegistrar(ApplicationContext applicationContext, RestClient restClient) {
		this.applicationContext = applicationContext;
		this.restClient = restClient;
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
		if (!DiscordInteractionPropertySupplier.class.isAssignableFrom(method.getReturnType())) {
			System.out.println(method.getReturnType().getName());
			throw new IllegalArgumentException("Return type for method %s in %s isn't assignable from %s".formatted(method.getName(), bean.getClass().getName(), DiscordInteractionPropertySupplier.class.getName()));
		}
		if (method.getParameterCount() == 0) {
			DiscordInteractionPropertySupplier<?> supplier = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean))
					.filter(o -> o instanceof DiscordInteractionPropertySupplier)
					.map(o -> (DiscordInteractionPropertySupplier<?>) o)
					.orElseThrow();
			final var mapper = JacksonResources.create();
			switch (supplier) {
				case Raw raw -> {
					var request = mapper.getObjectMapper()
							.readValue(raw.get(), ApplicationCommandRequest.class);
					commands.add(request);
				}
				case Resources resources -> {
					var matcher = new PathMatchingResourcePatternResolver();
					var commands = new ArrayList<ApplicationCommandRequest>();
					String[] locations = resources.get();
					for (String location : locations) {
						for (var resource : matcher.getResources(location)) {
							var request = mapper.getObjectMapper()
									.readValue(resource.getInputStream(), ApplicationCommandRequest.class);
							commands.add(request);
						}
					}
					this.commands.addAll(commands);
				}
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
		final ApplicationService applicationService = restClient.getApplicationService();
		final Optional<Long> applicationId = restClient.getApplicationId().blockOptional();
		if (applicationId.isEmpty()) {
			LOG.error("No application id available");
			return;
		}
		applicationService.bulkOverwriteGlobalApplicationCommand(applicationId.get(), commands)
				.doOnNext(data -> LOG.debug("Successfully registered %s".formatted(data.name())))
				.doOnError(e -> LOG.error("Failed to register global commands", e))
				.subscribe();
	}
}
