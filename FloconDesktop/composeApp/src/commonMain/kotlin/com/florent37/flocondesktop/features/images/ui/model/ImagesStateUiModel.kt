package com.florent37.flocondesktop.features.images.ui.model

sealed interface ImagesStateUiModel {
    data object Idle : ImagesStateUiModel

    data object Empty : ImagesStateUiModel

    data class WithImages(
        val images: List<ImagesUiModel>,
    ) : ImagesStateUiModel
}

fun previewImagesStateUiModel(items: Int = 10) = ImagesStateUiModel.WithImages(
    List(10) {
        previewImagesUiModel()
    },
)
