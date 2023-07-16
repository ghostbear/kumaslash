package me.ghostbear.kumaslash.data.tachiyomi.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ghostbear.kumaslash.configuration.TachiyomiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
public class TachiyomiWebClient {

	private final String organization;
	private final String repository;
	private final String branch;
	private final String location;

	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Autowired
	public TachiyomiWebClient(TachiyomiProperties tachiyomiProperties, WebClient webClient, ObjectMapper objectMapper) {
		this.organization = tachiyomiProperties.getExtensions().getOrganization();
		this.repository = tachiyomiProperties.getExtensions().getRepository();
		this.branch = tachiyomiProperties.getExtensions().getBranch();
		this.location = tachiyomiProperties.getExtensions().getLocation();
		this.webClient = webClient;
		this.objectMapper = objectMapper;
	}

	public Mono<List<ExtensionDTO>> getExtensions() {
		return webClient.get()
				.uri("https://raw.githubusercontent.com/%s/%s/%s/%s".formatted(organization, repository, branch, location))
				.accept(MediaType.APPLICATION_JSON, MediaType.valueOf("application/vnd.github+json"))
				.exchangeToMono(clientResponse -> {
					if (clientResponse.statusCode().is2xxSuccessful()) {
						return clientResponse.bodyToMono(String.class)
								.map(string -> {
									try {
										return objectMapper.readValue(string, new TypeReference<>() {
										});
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								});
					} else {
						return clientResponse.createException()
								.flatMap(Mono::error);
					}
				});
	}

}
