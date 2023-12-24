/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash;

import java.io.PrintStream;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;

public class KumaSlashBanner implements Banner {

	private static final String[] BANNER =
			new String[] {
				"       .                      *      |  ",
				"  *                 .               -O- ",
				"            o                        |  ",
				"      .                          .      ",
				"                       *              o ",
				" _  __                        __  __  __",
				"| |/ /   _ _ __ ___   __ _   / / / / / /",
				"| ' / | | | '_ ` _ \\ / _` | / / / / / /",
				"| . \\ |_| | | | | | | (_| |/ / / / / /",
				"|_|\\_\\__,_|_| |_| |_|\\__,_/ / / / / /",
				"=========================/_/=/_/=/_/",
			};
	private static final String SPRING_BOOT = ":: Spring Boot ::";

	@Override
	public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
		String[] banner = BANNER;
		for (String line : banner) {
			out.println(line);
		}
		String version = "(v" + SpringBootVersion.getVersion() + ")";

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.repeat(" ", 40 - (version.length() + SPRING_BOOT.length()));
		stringBuilder.insert(0, SPRING_BOOT);
		stringBuilder.insert(40 - version.length(), version);
		out.println(stringBuilder);
	}
}
