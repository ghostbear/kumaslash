package me.ghostbear.kumaslash.guild.services;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.SequenceInputStream;

@Service
public class EmojiService {

	private static final String EMOJI_ENDPOINT = "https://cdn.discordapp.com/emojis/%s.%s?v=1";

	private final WebClient webClient;

	public EmojiService(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<InputStream> getEmojiAsInputStream(String name, String format) {
		return webClient.get()
				.uri(EMOJI_ENDPOINT.formatted(name, format))
				.retrieve()
				.bodyToFlux(DataBuffer.class)
				.map(b -> b.asInputStream(true))
				.reduce(SequenceInputStream::new);
	}

}
