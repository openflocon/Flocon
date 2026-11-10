package io.github.openflocon.flocondesktop.features.images.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class ImageViewerUiModel(
    val current: ImagesUiModel,
    // Same order as the screen list, used for the filmstrip.
    val images: ImmutableList<ImagesUiModel>,
    val currentIndex: Int,
    // "8 / 42"
    val positionText: String,
    val previousId: String?,
    val nextId: String?,
)

fun previewImageViewerUiModel(
    count: Int = 8,
    currentIndex: Int = 3,
): ImageViewerUiModel {
    val images = previewImagesStateUiModel(items = count).images
    return ImageViewerUiModel(
        current = images[currentIndex],
        images = images.toImmutableList(),
        currentIndex = currentIndex,
        positionText = "${currentIndex + 1} / $count",
        previousId = images.getOrNull(currentIndex - 1)?.id,
        nextId = images.getOrNull(currentIndex + 1)?.id,
    )
}
