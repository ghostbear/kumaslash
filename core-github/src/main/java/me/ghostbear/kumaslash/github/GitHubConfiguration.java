package me.ghostbear.kumaslash.github;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class GitHubConfiguration {

	@NotNull
	@Bean("githubWebClient")
	WebClient githubWebClient() {
		return WebClient.builder()
				.baseUrl("https://api.github.com")
				.build();
	}

	@NotNull
	@Bean("gitHubService")
	GitHubService gitHubService(@Qualifier("githubWebClient") WebClient githubWebClient) {
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(githubWebClient))
				.build();
		return httpServiceProxyFactory.createClient(GitHubService.class);
	}

}
