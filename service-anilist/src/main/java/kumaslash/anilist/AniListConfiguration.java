/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.anilist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class AniListConfiguration {

	@Bean
	HttpGraphQlClient aniListGraphQlClient() {
		return HttpGraphQlClient.builder(WebClient.builder()
						.defaultStatusHandler(
								httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.NOT_FOUND),
								clientResponse -> Mono.empty()))
				.url("https://graphql.anilist.co")
				.build();
	}
}
