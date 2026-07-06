package io.github.openflocon.library.designsystem.common

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

actual fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val stringSelection = StringSelection(text)

    clipboard.setContents(stringSelection, null)
}

actual fun readFromClipboard(): String? {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    return try {
        clipboard.getData(DataFlavor.stringFlavor) as String
    } catch (e: Exception) {
        null
    }
}
