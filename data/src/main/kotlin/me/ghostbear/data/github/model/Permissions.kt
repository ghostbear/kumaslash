package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Permissions(
    @SerialName("admin")
    val admin: Boolean = false,
    @SerialName("push")
    val push: Boolean = false,
    @SerialName("pull")
    val pull: Boolean = false
)