package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException


import java.io.File
import java.util.concurrent.TimeUnit

actual fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val stringSelection = StringSelection(text)
    clipboard.setContents(stringSelection, null)
}

actual fun copyToClipboard(bitmap: ImageBitmap) {
    try {
        val bufferedImage = ImageOptimizer.toOptimizedBufferedImage(bitmap)

        val osName = System.getProperty("os.name")
        val isMac = osName != null && osName.contains("Mac", ignoreCase = true)

        if (isMac) {
            var nativeCopied = false
            try {
                val tempFile = File.createTempFile("flocon_clipboard", ".png")
                tempFile.deleteOnExit()
                if (ImageOptimizer.saveToFile(bufferedImage, tempFile)) {
                    val script = "set the clipboard to (read (POSIX file \"${tempFile.absolutePath}\") as «class PNGf»)"
                    val process = ProcessBuilder("osascript", "-e", script).start()
                    val finished = process.waitFor(2, TimeUnit.SECONDS)
                    if (finished && process.exitValue() == 0) {
                        nativeCopied = true
                    }
                }
                tempFile.delete()
            } catch (e: Exception) {
                // Fallback to standard AWT clipboard below
            }
            if (nativeCopied) {
                return
            }
        }

        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val imageSelection = ImageSelection(bufferedImage)
        clipboard.setContents(imageSelection, null)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


private class ImageSelection(
    private val image: Image,
) : Transferable {

    override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.imageFlavor)

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor == DataFlavor.imageFlavor

    override fun getTransferData(flavor: DataFlavor): Any {
        if (flavor == DataFlavor.imageFlavor) {
            return image
        }
        throw UnsupportedFlavorException(flavor)
    }
}


actual fun readFromClipboard(): String? {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    return try {
        clipboard.getData(DataFlavor.stringFlavor) as String
    } catch (e: Exception) {
        null
    }
}



