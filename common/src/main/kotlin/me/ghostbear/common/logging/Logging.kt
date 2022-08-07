package me.ghostbear.common.logging

import kotlinx.serialization.Serializable

@Serializable
data class Logging(
    val log: Log,
    val guildId: Long,
    val channelId: Long
)