package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewComment(
    @SerialName("href")
    val href: String = ""
)