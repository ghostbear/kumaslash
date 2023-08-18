package me.ghostbear.kumaslash.commands.core;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import reactor.core.publisher.Mono;

public non-sealed interface GuildCreateEventHandler extends EventHandler {

	Mono<Void> handle(GuildCreateEvent event);

}
