package io.github.openflocon.flocondesktop.common.utils

import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.path
import java.io.File

object FolderPicker {

    private const val TAG = "FolderPicker"

    suspend fun pickFolder(
        title: String = "Select Folder",
        initialPath: String? = null,
    ): File? = try {
        val initialDirectory = initialPath?.takeIf { it.isNotBlank() }?.let { PlatformFile(File(it)) }
        val directory = FileKit.openDirectoryPicker(
            directory = initialDirectory,
        )
        if (directory != null) {
            Logger.d(TAG) { "Folder picked via FileKit: ${directory.path}" }
            File(directory.path)
        } else {
            Logger.d(TAG) { "Folder picking cancelled" }
            null
        }
    } catch (t: Throwable) {
        Logger.e(TAG, t) { "Error while picking folder via FileKit" }
        null
    }
}
