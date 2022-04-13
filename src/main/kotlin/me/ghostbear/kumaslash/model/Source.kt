package me.ghostbear.kumaslash.model

import kotlinx.serialization.Serializable

@Serializable
data class Source (
    val name: String,
    val lang: String,
    val id: String,
    val baseUrl: String
)