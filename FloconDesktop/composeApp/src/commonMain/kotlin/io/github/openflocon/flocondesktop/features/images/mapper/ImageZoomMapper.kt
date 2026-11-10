package io.github.openflocon.flocondesktop.features.images.mapper

import kotlin.math.roundToInt

/** Zoom as a percentage ("200%"), or null when the image is fitted to the viewer. */
fun formatImageZoom(scale: Float): String? = if (scale <= 1f) null else "${(scale * 100).roundToInt()}%"
