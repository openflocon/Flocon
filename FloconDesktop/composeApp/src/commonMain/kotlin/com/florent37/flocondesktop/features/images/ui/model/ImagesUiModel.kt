package com.florent37.flocondesktop.features.images.ui.model

data class ImagesUiModel(
    val url: String,
    val downloadedAt: String,
)

fun previewImagesUiModel() = ImagesUiModel(
    url = "http://imageUrl.com/id",
    downloadedAt = "00:00:00.000",
)
