package io.github.openflocon.flocondesktop.features.images.mapper

import io.github.openflocon.flocondesktop.features.images.model.ImageViewerUiModel
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import kotlinx.collections.immutable.ImmutableList

/** Builds the viewer for [selectedId], or null when the image is not in [images] anymore. */
internal fun buildImageViewer(
    images: ImmutableList<ImagesUiModel>,
    selectedId: String,
): ImageViewerUiModel? {
    val index = images.indexOfFirst { it.id == selectedId }
    if (index < 0) return null
    return ImageViewerUiModel(
        current = images[index],
        images = images,
        currentIndex = index,
        positionText = formatImagePosition(index = index, count = images.size),
        previousId = images.getOrNull(index - 1)?.id,
        nextId = images.getOrNull(index + 1)?.id,
    )
}

/** "8 / 42" for the zero-based [index]. */
internal fun formatImagePosition(index: Int, count: Int): String = "${index + 1} / $count"
