package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.core.FileKit

actual suspend fun saveImageToFile(bitmap: ImageBitmap, defaultFileName: String): Boolean {
    return try {
        val baseName = defaultFileName.removeSuffix(".png")
        val savedFile = FileKit.saveFile(
            baseName = baseName,
            extension = "png",
        ) ?: return false

        val bufferedImage = ImageOptimizer.toOptimizedBufferedImage(bitmap)
        ImageOptimizer.saveToFile(bufferedImage, savedFile.file)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
