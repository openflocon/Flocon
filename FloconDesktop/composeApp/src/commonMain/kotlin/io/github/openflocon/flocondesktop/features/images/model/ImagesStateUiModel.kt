package io.github.openflocon.flocondesktop.features.images.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
sealed interface ImagesStateUiModel {
    data object Idle : ImagesStateUiModel

    data object Empty : ImagesStateUiModel

    data class WithImages(
        val images: ImmutableList<ImagesUiModel>,
    ) : ImagesStateUiModel
}

fun previewImagesStateUiModel(items: Int = 10) = ImagesStateUiModel.WithImages(
    List(items) { index ->
        val base = previewImagesUiModels()[index % 3]
        base.copy(id = "${base.id}-$index")
    }.toImmutableList(),
)
