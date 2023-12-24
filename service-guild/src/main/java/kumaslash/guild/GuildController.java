/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.guild;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kumaslash.jda.annotations.EventMapping;
import kumaslash.jda.annotations.JDAController;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;

@JDAController
public class GuildController {

	private final GuildNotifierService notifierService;
	private final GuildRepository guildRepository;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public GuildController(GuildNotifierService notifierService, GuildRepository guildRepository) {
		this.notifierService = notifierService;
		this.guildRepository = guildRepository;
	}

	@EventMapping
	public void onGuildReadyEvent(GuildReadyEvent event) {
		executor.execute(
				() -> {
					long snowflake = event.getGuild().getIdLong();
					boolean isNotNew = guildRepository.existsById(snowflake);
					guildRepository.save(new Guild(snowflake, !isNotNew));
					notifierService.notify(snowflake);
				});
	}
}
