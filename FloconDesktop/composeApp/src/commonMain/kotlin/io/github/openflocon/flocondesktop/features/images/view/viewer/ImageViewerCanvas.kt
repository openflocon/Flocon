package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.images.model.ImagesUiModel
import io.github.openflocon.flocondesktop.features.images.view.rememberImageRequest
import io.github.openflocon.library.designsystem.FloconTheme
import kotlin.math.pow

private const val ZoomPerScrollUnit = 1.1f

@Composable
internal fun ImageViewerCanvas(
    model: ImagesUiModel,
    zoomState: ImageZoomState,
    modifier: Modifier = Modifier,
) {
    // last known pointer position, relative to the canvas center: the pinch zooms around it
    val pointer = remember { mutableStateOf(Offset.Zero) }
    PinchZoomEffect { factor -> zoomState.zoomBy(factor, pointer.value) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { zoomState.viewportSize = it }
            .pointerInput(zoomState) {
                // two-finger scroll / wheel pans (zoomed only), ctrl or cmd + wheel zooms around the pointer
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        when (event.type) {
                            PointerEventType.Move, PointerEventType.Enter -> pointer.value = change.position.fromCenter(size)

                            PointerEventType.Scroll -> {
                                if (event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed) {
                                    val factor = ZoomPerScrollUnit.pow(-change.scrollDelta.y)
                                    zoomState.zoomBy(factor, change.position.fromCenter(size))
                                } else {
                                    zoomState.panByScroll(change.scrollDelta)
                                }
                                change.consume()
                            }
                        }
                    }
                }
            }
            .pointerInput(zoomState) {
                detectTapGestures(onDoubleTap = { zoomState.toggleZoom(it.fromCenter(size)) })
            }
            .pointerInput(zoomState) {
                detectDragGestures { change, dragAmount ->
                    if (zoomState.isZoomed) {
                        change.consume()
                        zoomState.panBy(dragAmount)
                    }
                }
            }
            .pointerHoverIcon(if (zoomState.isZoomed) PointerIcon.Hand else PointerIcon.Default),
    ) {
        if (isInPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FloconTheme.colors.bg300),
            )
        } else {
            AsyncImage(
                model = rememberImageRequest(model),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomState.scale
                        scaleY = zoomState.scale
                        translationX = zoomState.offset.x
                        translationY = zoomState.offset.y
                    },
            )
        }
    }
}

private fun Offset.fromCenter(size: IntSize) = this - Offset(size.width / 2f, size.height / 2f)
