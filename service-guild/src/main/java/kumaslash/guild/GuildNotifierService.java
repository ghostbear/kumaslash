/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.guild;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class GuildNotifierService {

	private static final Logger LOG = LoggerFactory.getLogger(GuildNotifierService.class);

	private static final String TABLE_NAME = "guild";

	private final JdbcTemplate template;

	public GuildNotifierService(JdbcTemplate template) {
		this.template = template;
	}

	@Transactional
	public void notify(Long snowflake) {
		template.execute("NOTIFY " + TABLE_NAME + ", '" + snowflake + "'");
	}

	public Runnable createNotificationHandler(Consumer<PGNotification> consumer) {
		return () -> {
			template.execute((Connection con) -> {
				try (Statement statement = con.createStatement()) {
					statement.execute("LISTEN " + TABLE_NAME);

					PGConnection pgCon = con.unwrap(PGConnection.class);
					while (!Thread.interrupted()) {
						PGNotification[] notifications = pgCon.getNotifications();
						if (notifications == null) {
							continue;
						}
						for (PGNotification notification : notifications) {
							consumer.accept(notification);
						}
					}

					return 0;
				} catch (SQLException e) {
					LOG.error("Error executing SQL statement", e);
					return 0;
				}
			});
		};
	}
}
