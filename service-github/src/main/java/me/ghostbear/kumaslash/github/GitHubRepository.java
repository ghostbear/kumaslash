package me.ghostbear.kumaslash.github;

import me.ghostbear.kumaslash.github.model.Issue;
import me.ghostbear.kumaslash.github.model.Release;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GitHubRepository {

	private static final String BASE_URL = "https://api.github.com";

	private final WebClient webClient;

	public GitHubRepository(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<Issue> getIssue(String owner, String repository, String issueNumber) {
		return webClient.get()
				.uri("%s/repos/%s/%s/issues/%s".formatted(BASE_URL, owner, repository, issueNumber))
				.exchangeToMono(clientResponse -> {
					if (clientResponse.statusCode().is2xxSuccessful()) {
						return clientResponse.bodyToMono(new ParameterizedTypeReference<>() {
						});
					}
					return clientResponse.createException()
							.flatMap(Mono::error);
				});
	}

	public Mono<Release> getLatestRelease(String owner, String repository) {
		return webClient.get()
				.uri("%s/repos/%s/%s/releases/latest".formatted(BASE_URL, owner, repository))
				.exchangeToMono(clientResponse -> {
					if (clientResponse.statusCode().is2xxSuccessful()) {
						return clientResponse.bodyToMono(new ParameterizedTypeReference<>() {
						});
					}
					return clientResponse.createException()
							.flatMap(Mono::error);
				});
	}

}
