/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.guild;

import com.zaxxer.hikari.util.DriverDataSource;
import java.util.Properties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class GuildConfiguration {

	@Bean
	GuildNotifierService guildNotifierService(DataSourceProperties properties) {
		DriverDataSource dataSource =
				new DriverDataSource(
						properties.determineUrl(),
						properties.determineDriverClassName(),
						new Properties(),
						properties.determineUsername(),
						properties.determinePassword());

		JdbcTemplate template = new JdbcTemplate(dataSource);

		return new GuildNotifierService(template);
	}

	@Bean
	CommandLineRunner guildNotifierServiceRunner(
			GuildNotifierService notifierService, GuildNotificationHandler notificationHandler) {
		return args -> {
			Runnable listener = notifierService.createNotificationHandler(notificationHandler);
			Thread thread = new Thread(listener, "guild-listener");
			thread.start();
		};
	}
}
