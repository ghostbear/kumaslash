package me.ghostbear.common.extensions

import kotlinx.serialization.Serializable

@Serializable
data class Match<T>(
    val value: T,
    val highlight: String
)