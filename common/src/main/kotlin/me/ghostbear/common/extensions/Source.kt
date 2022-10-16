package me.ghostbear.common.extensions

import kotlinx.serialization.Serializable

@Serializable
data class Source(
    val id: String,
    val name: String,
    val apk: String,
    val pkg: String,
    val version: String,
    val lang: String,
)
