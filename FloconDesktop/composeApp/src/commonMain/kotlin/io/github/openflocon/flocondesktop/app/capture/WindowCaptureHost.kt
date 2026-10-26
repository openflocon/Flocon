package io.github.openflocon.flocondesktop.app.capture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.CancellationException

/**
 * Records what [content] displays in a graphics layer, to answer the requests of [controller] with an image of it.
 *
 * Only the visible content is captured: windows and dialogs opened apart are not part of it.
 */
@Composable
fun WindowCaptureHost(
    controller: WindowCaptureController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(controller, graphicsLayer) {
        for (request in controller.requests) {
            // a request that timed out is cancelled, no need to render it
            if (!request.isActive) continue
            try {
                request.complete(graphicsLayer.toImageBitmap())
            } catch (e: CancellationException) {
                request.cancel(e)
                throw e
            } catch (t: Throwable) {
                request.completeExceptionally(t)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(graphicsLayer)
            },
    ) {
        content()
    }
}
