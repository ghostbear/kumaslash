package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reactions(
    @SerialName("url")
    val url: String = "",
    @SerialName("total_count")
    val totalCount: Int = 0,
    @SerialName("+1")
    val positiveVotes: Int = 0,
    @SerialName("-1")
    val negativeVotes: Int = 0,
    @SerialName("laugh")
    val laugh: Int = 0,
    @SerialName("hooray")
    val hooray: Int = 0,
    @SerialName("confused")
    val confused: Int = 0,
    @SerialName("heart")
    val heart: Int = 0,
    @SerialName("rocket")
    val rocket: Int = 0,
    @SerialName("eyes")
    val eyes: Int = 0
)