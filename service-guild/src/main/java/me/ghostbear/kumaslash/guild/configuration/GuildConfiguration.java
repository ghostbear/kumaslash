package me.ghostbear.kumaslash.guild.configuration;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcRepositories(entityOperationsRef = "guildEntityTemplate", basePackages = "me.ghostbear.kumaslash.guild")
public class GuildConfiguration {

	@Bean(name = "guildR2dbcProperties")
	@ConfigurationProperties("spring.r2dbc.guild")
	public R2dbcProperties guildR2dbcProperties() {
		return new R2dbcProperties();
	}

	@Bean(name = "guildFlywayProperties")
	@ConfigurationProperties("spring.r2dbc.guild.flyway")
	public FlywayProperties guildFlywayProperties() {
		return new FlywayProperties();
	}

	@Bean(name = "guildFlywayConnectionDetails")
	@ConditionalOnMissingBean(FlywayConnectionDetails.class)
	FlywayConnectionDetails guildFlywayConnectionDetails(@Qualifier("guildFlywayProperties") FlywayProperties properties) {
		return new FlywayConnectionDetails() {

			@Override
			public String getUsername() {
				return properties.getUser();
			}

			@Override
			public String getPassword() {
				return properties.getPassword();
			}

			@Override
			public String getJdbcUrl() {
				return properties.getUrl();
			}

			@Override
			public String getDriverClassName() {
				return properties.getDriverClassName();
			}
		};
	}

	@Bean(name = "guildFlywayInitializer")
	@ConditionalOnMissingBean
	public FlywayMigrationInitializer guildFlywayInitializer(
			@Qualifier("guildFlyway") Flyway flyway,
			ObjectProvider<FlywayMigrationStrategy> migrationStrategy
	) {
		return new FlywayMigrationInitializer(flyway, migrationStrategy.getIfAvailable());
	}

	@Bean(name = "guildFlyway", initMethod = "migrate")
	@Qualifier("guildFlyway")
	public Flyway guildFlyway(
			@Qualifier("guildFlywayProperties") FlywayProperties flywayProperties,
			@Qualifier("guildR2dbcProperties") R2dbcProperties r2dbcProperties
	) {
		return Flyway.configure()
				.dataSource(r2dbcProperties.getUrl().replace("r2dbc", "jdbc"), r2dbcProperties.getUsername(), r2dbcProperties.getPassword())
				.locations(flywayProperties.getLocations()
						.toArray(String[]::new))
				.baselineOnMigrate(true)
				.load();
	}


	public ConnectionFactory createConnectionFactory( R2dbcProperties r2dbcProperties) {
		ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(r2dbcProperties.getUrl());
		ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder().from(options);
		builder.option(ConnectionFactoryOptions.USER, r2dbcProperties.getUsername());
		builder.option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getPassword());
		return ConnectionFactories.get(builder.build());
	}

	@DependsOn("guildFlyway")
	@Bean(name = "guildConnectionFactory")
	ConnectionPool connectionFactory(@Qualifier("guildR2dbcProperties") R2dbcProperties properties) {
		ConnectionFactory connectionFactory = createConnectionFactory(properties);
		R2dbcProperties.Pool pool = properties.getPool();
		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
		ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
		map.from(pool.getMaxIdleTime()).to(builder::maxIdleTime);
		map.from(pool.getMaxLifeTime()).to(builder::maxLifeTime);
		map.from(pool.getMaxAcquireTime()).to(builder::maxAcquireTime);
		map.from(pool.getMaxCreateConnectionTime()).to(builder::maxCreateConnectionTime);
		map.from(pool.getInitialSize()).to(builder::initialSize);
		map.from(pool.getMaxSize()).to(builder::maxSize);
		map.from(pool.getValidationQuery()).whenHasText().to(builder::validationQuery);
		map.from(pool.getValidationDepth()).to(builder::validationDepth);
		map.from(pool.getMinIdle()).to(builder::minIdle);
		map.from(pool.getMaxValidationTime()).to(builder::maxValidationTime);
		return new ConnectionPool(builder.build());
	}

	@Bean(name = "guildEntityTemplate")
	public R2dbcEntityOperations guildEntityTemplate(@Qualifier("guildConnectionFactory") ConnectionFactory connectionFactory) {
		DefaultReactiveDataAccessStrategy strategy = new DefaultReactiveDataAccessStrategy(PostgresDialect.INSTANCE);
		DatabaseClient databaseClient = DatabaseClient.builder()
				.connectionFactory(connectionFactory)
				.bindMarkers(PostgresDialect.INSTANCE.getBindMarkersFactory())
				.build();
		return new R2dbcEntityTemplate(databaseClient, strategy);
	}

}
