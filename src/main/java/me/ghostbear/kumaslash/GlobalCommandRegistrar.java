package me.ghostbear.kumaslash;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class GlobalCommandRegistrar implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalCommandRegistrar.class);

	private final RestClient restClient;

	@Autowired
	public GlobalCommandRegistrar(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		final var mapper = JacksonResources.create();

		var matcher = new PathMatchingResourcePatternResolver();
		final var applicationService = restClient.getApplicationService();
		final long applicationId = restClient.getApplicationId().block();

		var commands = new ArrayList<ApplicationCommandRequest>();
		for (var resource : matcher.getResources("commands/*.json")) {
			var request = mapper.getObjectMapper()
					.readValue(resource.getInputStream(), ApplicationCommandRequest.class);
			commands.add(request);
		}

		applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
				.doOnNext(ignore -> LOGGER.debug("Successfully registered Global Commands"))
				.doOnError(e -> LOGGER.error("Failed to register global commands", e))
				.subscribe();
	}
}
