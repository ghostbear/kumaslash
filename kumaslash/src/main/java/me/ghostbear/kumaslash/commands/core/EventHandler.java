package me.ghostbear.kumaslash.commands.core;

public sealed interface EventHandler permits GuildCreateEventHandler, ModalEventHandler, SlashCommandEventHandler {
}
