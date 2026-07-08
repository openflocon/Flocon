package io.github.openflocon.flocondesktop.common.utils

import java.awt.FileDialog
import java.awt.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun pickAdbFile(): String? = withContext(Dispatchers.IO) {
    val fileDialog = FileDialog(null as Frame?, "Select ADB Executable", FileDialog.LOAD)
    fileDialog.isVisible = true
    val directory = fileDialog.directory
    val file = fileDialog.file
    if (directory != null && file != null) {
        "$directory$file"
    } else {
        null
    }
}
