package io.github.openflocon.flocondesktop.features.images.model

data class ImagesUiModel(
    val url: String,
    val downloadedAt: String,
)

fun previewImagesUiModel() = ImagesUiModel(
    url = "http://imageUrl.com/id",
    downloadedAt = "00:00:00.000",
)
