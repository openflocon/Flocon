package io.github.openflocon.library.designsystem.common

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

actual fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val stringSelection = StringSelection(text)

    clipboard.setContents(stringSelection, null)
}

actual fun copyToClipboard(bitmap: ImageBitmap) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val awtImage = bitmap.toAwtImage()
    val imageSelection = ImageSelection(awtImage)
    clipboard.setContents(imageSelection, null)
}

private class ImageSelection(private val image: Image) : Transferable {
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

