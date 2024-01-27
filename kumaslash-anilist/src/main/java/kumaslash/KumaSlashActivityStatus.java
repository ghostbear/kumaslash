/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash;

import jakarta.annotation.PostConstruct;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class KumaSlashActivityStatus {

	private final String[] values = {
		"Konjō no Gakki: Unmei ni Te wo Tsunagu Koi no Monogatari",
		"Mahō no Cafe: Yume no Chōchin e Kyōdai",
		"Mahō no Pātē: Maho no Furi o Tsuketa Oishi Mono o Tsukuru",
		"Jikan o Motsu Tsumugimono: Jidai o Koete Ai o Dakishimeru",
		"Ryū no Hokori: Gōryū no Sāga",
		"Kurogōku no Chronicle: Haguruma to Mahō no Monogatari",
		"Kosmosu Serenade: Sora no Gyoshi no Ballad"
	};

	private final JDA jda;

	public KumaSlashActivityStatus(JDA jda) {
		this.jda = jda;
	}

	@PostConstruct
	@Scheduled(cron = "@hourly")
	public void changeActivityStatus() {
		Random random = new Random();
		int i = random.nextInt(values.length);
		jda.getPresence().setActivity(Activity.watching(values[i]));
	}
}
