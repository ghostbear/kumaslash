package me.ghostbear.data.github.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Head(
    @SerialName("label")
    val label: String = "",
    @SerialName("ref")
    val ref: String = "",
    @SerialName("sha")
    val sha: String = "",
    @SerialName("user")
    val user: User = User(),
    @SerialName("repo")
    val repo: Repo = Repo()
)