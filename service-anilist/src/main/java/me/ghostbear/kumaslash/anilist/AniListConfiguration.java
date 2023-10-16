package me.ghostbear.kumaslash.anilist;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@ComponentScan
public class AniListConfiguration {

	@Bean
	@NotNull
	HttpGraphQlClient aniListGraphQlClient() {
		return HttpGraphQlClient.builder(
						WebClient.builder()
								.baseUrl("https://graphql.anilist.co")
								.defaultStatusHandler(
										httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
										clientResponse -> Mono.empty())
								.build()
				)
				.build();
	}

}
