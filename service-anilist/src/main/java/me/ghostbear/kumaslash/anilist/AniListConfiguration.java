package me.ghostbear.kumaslash.anilist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class AniListConfiguration {

	@Bean
	HttpGraphQlClient aniListGraphQlClient() {
		return HttpGraphQlClient.builder(
						WebClient.builder()
								.baseUrl("https://graphql.anilist.co")
								.build()
				)
				.build();
	}

}
