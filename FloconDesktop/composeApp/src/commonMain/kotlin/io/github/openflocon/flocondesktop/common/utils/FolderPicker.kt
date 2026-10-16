package io.github.openflocon.flocondesktop.common.utils

import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.core.FileKit
import java.io.File

object FolderPicker {

    private const val TAG = "FolderPicker"

    suspend fun pickFolder(
        title: String = "Select Folder",
        initialPath: String? = null,
    ): File? {
        return try {
            val directory = FileKit.pickDirectory(
                initialDirectory = initialPath?.takeIf { it.isNotBlank() },
                title = title,
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
}
