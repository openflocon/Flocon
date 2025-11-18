package io.github.openflocon.flocondesktop.features.images.model

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

data class ImagesUiModel(
    val url: String,
    val downloadedAt: String,
    val headers: PersistentMap<String, String>,
)

fun previewImagesUiModel() = ImagesUiModel(
    url = "http://imageUrl.com/id",
    downloadedAt = "00:00:00.000",
    headers = persistentMapOf(),
)
