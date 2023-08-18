package me.ghostbear.kumaslash.schedule;

import me.ghostbear.kumaslash.data.tachiyomi.TachiyomiExtensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TachiyomiExtensionSchedule {

	private static final Logger LOGGER = LoggerFactory.getLogger(TachiyomiExtensionSchedule.class);

	private final TachiyomiExtensionService tachiyomiExtensionService;

	@Autowired
	public TachiyomiExtensionSchedule(TachiyomiExtensionService tachiyomiExtensionService) {
		this.tachiyomiExtensionService = tachiyomiExtensionService;
	}

	@Scheduled(initialDelay = 15, fixedRate = 3600, timeUnit = TimeUnit.SECONDS)
	public void updateTachiyomiExtension() {
		tachiyomiExtensionService.update()
				.doOnSuccess(extensions -> LOGGER.info("Updated extensions in database"))
				.doOnError(e -> LOGGER.error("Failed to fetch extensions", e))
				.subscribe();
	}


}
