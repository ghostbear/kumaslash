package me.ghostbear.kumaslash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { R2dbcAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableScheduling
public class KumaslashNextApplication {

	public static void main(String[] args) {
		SpringApplication.run(KumaslashNextApplication.class, args);
	}

}
