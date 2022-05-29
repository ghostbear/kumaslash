package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Label(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("node_id")
    val nodeId: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("color")
    val color: String = "",
    @SerialName("default")
    val default: Boolean = false,
    @SerialName("description")
    val description: String = ""
)