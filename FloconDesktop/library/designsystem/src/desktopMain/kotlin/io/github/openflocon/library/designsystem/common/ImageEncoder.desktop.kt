package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap

actual fun encodeImageToPng(bitmap: ImageBitmap): EncodedImage {
    val image = ImageOptimizer.toOptimizedBufferedImage(bitmap)
    return EncodedImage(
        pngBytes = ImageOptimizer.toPngByteArray(image),
        width = image.width,
        height = image.height,
    )
}
