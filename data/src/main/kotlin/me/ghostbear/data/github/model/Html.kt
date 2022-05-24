package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Html(
    @SerialName("href")
    val href: String = ""
)