package me.ghostbear.common.download

import kotlinx.serialization.Serializable

@Serializable
enum class Repository {
    TACHIYOMI,
    TACHIYOMI_SY,
    TACHIYOMI_J2K,
    NEKO,
    TACHIYOMI_PREVIEW,
    TACHIYOMI_SY_PREVIEW
}