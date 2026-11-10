package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.runtime.Composable

/**
 * Listens to the trackpad pinch gesture (macOS only, no-op elsewhere) while in composition.
 * [onPinch] receives a multiplicative zoom factor: > 1 zooms in, < 1 zooms out.
 */
@Composable
expect fun PinchZoomEffect(onPinch: (zoomFactor: Float) -> Unit)
