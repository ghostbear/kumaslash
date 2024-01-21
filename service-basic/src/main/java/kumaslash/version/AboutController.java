/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.version;

import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.annotations.SlashCommandMapping;
import kumaslash.jda.events.CommandSupplier;
import kumaslash.jda.events.ResourceCommandSupplier;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@JDAController
public class AboutController {

	private final BuildProperties buildProperties;

	public AboutController(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@SlashCommandMapping(name = "about")
	public void about(SlashCommandInteractionEvent event) {
		StringBuilder builder = new StringBuilder();
		builder.append("# KumaSlash");
		builder.append("\n");
		builder.append("\n");
		String group = buildProperties.getGroup();
		append(builder, "Group", group);
		String name = buildProperties.getName();
		append(builder, "Name", name);
		String artifact = buildProperties.getArtifact();
		append(builder, "Artifact", artifact);
		String version = buildProperties.getVersion();
		append(builder, "Version", version);
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		String time = formatter.format(buildProperties.getTime().atZone(ZoneOffset.UTC));
		append(builder, "Time", time);
		builder.append("\n");
		String repository = "[ghostbear/kumaslash](<https://github.com/ghostbear/kumaslash>)";
		append(builder, "Source", repository);
		String creator = "[ghostbear](<https://github.com/ghostbear>)";
		append(builder, "Creator", creator);
		event.replyFormat(builder.toString()).queue();
	}

	private void append(StringBuilder builder, String left, String right) {
		builder.append("**");
		builder.append(left);
		builder.append(": ");
		builder.append("**");
		builder.append(right);
		builder.append("\n");
	}

	@Bean
	public CommandSupplier versionCommandSupplier() {
		return new ResourceCommandSupplier(new ClassPathResource("version.json"));
	}
}
