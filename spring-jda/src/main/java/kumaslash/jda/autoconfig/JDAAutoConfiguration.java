/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.autoconfig;

import kumaslash.jda.annotations.JDAController;
import kumaslash.jda.configuration.JDAProperties;
import kumaslash.jda.events.CommandSupplier;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(JDAProperties.class)
public class JDAAutoConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(JDAAutoConfiguration.class);

	private final JDAProperties jdaProperties;

	public JDAAutoConfiguration(JDAProperties jdaProperties) {
		this.jdaProperties = jdaProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public JDA jda(JDABuilder jdaBuilder) throws InterruptedException {
		return jdaBuilder.build().awaitReady();
	}

	@Bean
	@ConditionalOnMissingBean
	public JDABuilder jdaBuilder(
			ApplicationContext applicationContext, IEventManager eventManager) {
		return JDABuilder.createDefault(jdaProperties.getToken())
				.setEventManager(eventManager)
				.setEventPassthrough(true)
				.enableIntents(
						GatewayIntent.MESSAGE_CONTENT, GatewayIntent.AUTO_MODERATION_EXECUTION)
				.addEventListeners(applicationContext
						.getBeansWithAnnotation(JDAController.class)
						.values()
						.toArray());
	}

	@Bean
	public CommandLineRunner registerCommandsRunner(
			JDA jda, List<CommandSupplier> commandSuppliers) {
		return args -> {
			jda.updateCommands()
					.addCommands(
							commandSuppliers.stream().map(CommandSupplier::get).toList())
					.queue(commands -> LOG.info("Registered global commands: "
							+ commands.stream().map(Command::getName).toList()));
		};
	}
}
