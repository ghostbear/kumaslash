/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.guild;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GuildNotificationHandler implements Consumer<PGNotification> {

	private static final Logger LOG = LoggerFactory.getLogger(GuildNotificationHandler.class);

	private final JDA jda;
	private final GuildRepository repository;
	private final List<GuildCommandSupplier> guildCommandSuppliers;

	public GuildNotificationHandler(
			JDA jda, GuildRepository repository, List<GuildCommandSupplier> guildCommandSuppliers) {
		this.jda = jda;
		this.repository = repository;
		this.guildCommandSuppliers = guildCommandSuppliers;
	}

	@Override
	public void accept(PGNotification notification) {
		long snowflake = Long.parseLong(notification.getParameter());
		boolean exists = repository.existsById(snowflake);
		if (!exists) {
			LOG.warn("Guild doesn't exists, guild_id=" + snowflake);
			return;
		}

		if (guildCommandSuppliers.isEmpty()) {
			LOG.info("No GuildCommandSuppliers are registered, ignoring notification.");
			return;
		}

		Objects.requireNonNull(jda.getGuildById(snowflake), "Guild not found!")
				.updateCommands()
				.addCommands(
						guildCommandSuppliers.stream()
								.map(supplier -> supplier.apply(snowflake))
								.toList())
				.queue(
						commands ->
								LOG.info(
										"Registered guild ("
												+ snowflake
												+ ") commands: "
												+ commands.stream()
														.map(Command::getName)
														.toList()));
	}
}
