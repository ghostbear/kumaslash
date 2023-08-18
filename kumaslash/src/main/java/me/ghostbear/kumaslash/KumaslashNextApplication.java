package me.ghostbear.kumaslash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KumaslashNextApplication {

	public static void main(String[] args) {
		SpringApplication.run(KumaslashNextApplication.class, args);
	}

}
