package me.ghostbear.common.download

import kotlinx.serialization.Serializable

@Serializable
data class DownloadParams(
    val repository: Repository
)