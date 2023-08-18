package me.ghostbear.kumaslash.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({ R2dbcProperties.class, FlywayProperties.class })
public class KumaSlashConfiguration {

	@Bean
	public WebClient webClient() {
		final int size = 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.build();
	}

	@Bean(initMethod = "migrate")
	public Flyway flyway(FlywayProperties flywayProperties, R2dbcProperties r2dbcProperties) {
		return Flyway.configure()
				.dataSource(r2dbcProperties.getUrl().replace("r2dbc", "jdbc"), r2dbcProperties.getUsername(), r2dbcProperties.getPassword())
				.locations(flywayProperties.getLocations()
						.toArray(String[]::new))
				.baselineOnMigrate(true)
				.load();
	}

}
