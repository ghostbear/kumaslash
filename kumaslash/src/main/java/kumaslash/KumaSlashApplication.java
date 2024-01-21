/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KumaSlashApplication extends ListenerAdapter {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(KumaSlashApplication.class);
		application.setBanner(new KumaSlashBanner());
		application.run(args);
	}
}
