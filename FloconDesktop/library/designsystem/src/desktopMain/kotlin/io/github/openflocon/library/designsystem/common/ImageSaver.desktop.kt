package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.path
import java.io.File

actual suspend fun saveImageToFile(bitmap: ImageBitmap, defaultFileName: String): Boolean {
    return try {
        val baseName = defaultFileName.removeSuffix(".png")
        val savedFile = FileKit.openFileSaver(
            suggestedName = baseName,
            defaultExtension = "png",
        ) ?: return false

        val bufferedImage = ImageOptimizer.toOptimizedBufferedImage(bitmap)
        ImageOptimizer.saveToFile(bufferedImage, File(savedFile.path))
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
