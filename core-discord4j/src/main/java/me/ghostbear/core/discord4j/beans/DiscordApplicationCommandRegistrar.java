package me.ghostbear.core.discord4j.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import jakarta.annotation.PostConstruct;
import me.ghostbear.core.discord4j.utils.DiscordInteractionPropertySupplier;
import me.ghostbear.core.discord4j.utils.Raw;
import me.ghostbear.core.discord4j.utils.Resources;
import me.ghostbear.core.discord4j.annotations.DiscordComponent;
import me.ghostbear.core.discord4j.annotations.DiscordInteractionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.*;

public class DiscordApplicationCommandRegistrar {

	private static final Logger LOG = LoggerFactory.getLogger(DiscordApplicationCommandRegistrar.class);

	private final ApplicationContext applicationContext;
	private final RestClient restClient;

	public DiscordApplicationCommandRegistrar(ApplicationContext applicationContext, RestClient restClient) {
		this.applicationContext = applicationContext;
		this.restClient = restClient;
	}

	@PostConstruct
	public void init() {
		final ApplicationService applicationService = restClient.getApplicationService();
		final Optional<Long> applicationId = restClient.getApplicationId().blockOptional();
		if (applicationId.isEmpty()) {
			LOG.error("No application id available");
			return;
		}
		Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(DiscordComponent.class);
		Flux.fromIterable(beansWithAnnotation.values())
				.flatMap(DiscordApplicationCommandRegistrar.this::processBean)
				.buffer()
				.flatMap(l -> {
					LOG.debug("{} commands detected", l.size());
					return applicationService.bulkOverwriteGlobalApplicationCommand(applicationId.get(), l);
				})
				.onErrorContinue((throwable, object) -> LOG.error("Failed to register commands", throwable))
				.subscribe();
	}

	public Flux<ApplicationCommandRequest> processBean(Object bean) throws BeansException {
		return Flux.just(bean)
				.filter(o -> AnnotationUtils.isAnnotationDeclaredLocally(DiscordComponent.class, o.getClass()))
				.flatMapIterable(o -> Arrays.asList(ReflectionUtils.getDeclaredMethods(o.getClass())))
				.filter(m -> m.isAnnotationPresent(DiscordInteractionProperties.class))
				.flatMap(m -> doDiscordApplicationCommandProperties(m, bean));
	}

	private Flux<ApplicationCommandRequest> doDiscordApplicationCommandProperties(Method method, Object bean) {
		if (!DiscordInteractionPropertySupplier.class.isAssignableFrom(method.getReturnType())) {
			return Flux.error(new IllegalArgumentException("Return type for method %s in %s isn't assignable from %s".formatted(method.getName(), bean.getClass().getName(), DiscordInteractionPropertySupplier.class.getName())));
		}

		if (method.getParameterCount() != 0) {
			return Flux.empty();
		}

		DiscordInteractionPropertySupplier<?> supplier = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean))
				.filter(o -> o instanceof DiscordInteractionPropertySupplier)
				.map(o -> (DiscordInteractionPropertySupplier<?>) o)
				.orElse(null);

		if (Objects.isNull(supplier)) {
			return Flux.error(new NullPointerException("Invoking properties method yielded null value"));
		}

		final var mapper = JacksonResources.create();
		return switch (supplier) {
			case Raw raw -> doRawSupplier(mapper.getObjectMapper(), raw);
			case Resources resources -> doResourcesSupplier(mapper.getObjectMapper(), resources);
		};
	}

	private Flux<ApplicationCommandRequest> doRawSupplier(ObjectMapper objectMapper, Raw raw) {
		return Flux.generate(sink -> {
			try {
				ApplicationCommandRequest applicationCommandRequest = objectMapper.readValue(raw.get(), ApplicationCommandRequest.class);
				sink.next(applicationCommandRequest);
			} catch (Exception e) {
				sink.error(e);
			}
			sink.complete();
		});
	}

	private Flux<ApplicationCommandRequest> doResourcesSupplier(ObjectMapper objectMapper, Resources resources) {
		var matcher = new PathMatchingResourcePatternResolver();
		return Flux.just(resources.get())
				.handle((location, sink) -> {
					try {
						Resource resource = matcher.getResource(location);
						sink.next(objectMapper.readValue(resource.getInputStream(), ApplicationCommandRequest.class));
					} catch (Exception e) {
						sink.error(e);
					}
				});
	}

}
