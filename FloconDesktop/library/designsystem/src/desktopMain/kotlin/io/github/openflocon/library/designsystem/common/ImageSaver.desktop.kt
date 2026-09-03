package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

actual fun saveImageToFile(bitmap: ImageBitmap, defaultFileName: String): Boolean {
    return try {
        val parentFrame = Frame()
        val dialog = FileDialog(parentFrame, "Save Image", FileDialog.SAVE).apply {
            file = defaultFileName
        }
        dialog.isVisible = true
        val file = dialog.file
        val directory = dialog.directory
        parentFrame.dispose()

        if (file != null && directory != null) {
            val targetFile = File(directory, if (file.endsWith(".png", ignoreCase = true)) file else "$file.png")
            val bufferedImage = ImageOptimizer.toOptimizedBufferedImage(bitmap)
            ImageOptimizer.saveToFile(bufferedImage, targetFile)
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


