package me.ghostbear.kumaslash.anilist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KumaslashAniListApplication {

	public static void main(String[] args) {
		SpringApplication.run(KumaslashAniListApplication.class, args);
	}

}
