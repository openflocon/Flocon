package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap

/**
 * A PNG, scaled down if the image is too big
 */
class EncodedImage(
    val pngBytes: ByteArray,
    val width: Int,
    val height: Int,
)

expect fun encodeImageToPng(bitmap: ImageBitmap): EncodedImage
