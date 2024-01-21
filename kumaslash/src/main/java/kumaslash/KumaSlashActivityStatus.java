/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class KumaSlashActivityStatus {

	private final String[] values = {
		"Brushing fur!",
		"Scratching back against tree!",
		"Sharpening claws!",
		"Catching salmon down stream!",
		"Eating blueberries from humans!",
		"Raiding trashcans!"
	};

	private final JDA jda;

	public KumaSlashActivityStatus(JDA jda) {
		this.jda = jda;
	}

	@Scheduled(cron = "* */5 * * * *")
	public void changeActivityStatus() {
		Random random = new Random();
		int i = random.nextInt(values.length);
		jda.getPresence().setActivity(Activity.customStatus(values[i]));
	}
}
