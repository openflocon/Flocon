package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

const val ImageFitScale = 1f
const val ImageMaxScale = 8f
const val ImageDoubleTapScale = 2f
const val ImageZoomStep = 1.25f
const val ImageScrollPanPx = 20f

/**
 * Zoom and pan of the image shown in the viewer. Scale 1 is "fit". The image is transformed around the center
 * of the viewport: a point p is displayed at center + offset + scale * (p - center).
 * Focus points are expressed relative to the viewport center.
 */
@Stable
class ImageZoomState {
    var scale by mutableFloatStateOf(ImageFitScale)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set
    var viewportSize by mutableStateOf(IntSize.Zero)

    val isZoomed: Boolean get() = scale > ImageFitScale

    fun zoomBy(factor: Float, focus: Offset = Offset.Zero) = zoomTo(scale * factor, focus)

    fun zoomTo(target: Float, focus: Offset = Offset.Zero) {
        val newScale = target.coerceIn(ImageFitScale, ImageMaxScale)
        val ratio = newScale / scale
        scale = newScale
        offset = clampOffset(focus - (focus - offset) * ratio)
    }

    fun panBy(delta: Offset) {
        offset = clampOffset(offset + delta)
    }

    /** Pans by a scroll wheel / two-finger scroll delta: scrolling down moves the image up. */
    fun panByScroll(scrollDelta: Offset) = panBy(-scrollDelta * ImageScrollPanPx)

    fun toggleZoom(focus: Offset) {
        if (isZoomed) reset() else zoomTo(ImageDoubleTapScale, focus)
    }

    fun reset() {
        scale = ImageFitScale
        offset = Offset.Zero
    }

    private fun clampOffset(value: Offset): Offset {
        val maxX = viewportSize.width * (scale - ImageFitScale) / 2f
        val maxY = viewportSize.height * (scale - ImageFitScale) / 2f
        // "+ 0f" turns a negative zero into a plain zero
        return Offset(value.x.coerceIn(-maxX, maxX) + 0f, value.y.coerceIn(-maxY, maxY) + 0f)
    }
}
