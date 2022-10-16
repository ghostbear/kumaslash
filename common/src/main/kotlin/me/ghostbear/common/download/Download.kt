package me.ghostbear.common.download

import kotlinx.serialization.Serializable

@Serializable
data class Download(
    val name: String,
    val repository: Repository,
    val downloadUrl: String,
    val changelogUrl: String
)