package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.files.model.FloconFolderModel

/**
 * Shows a folder in the Files section of the Flocon desktop app, where its content can be browsed,
 * downloaded and deleted. The folder itself is never deleted.
 *
 * Registering the same folder again replaces its name. A folder that does not exist is not shown.
 */
fun floconRegisterFolder(folder: FloconFolderModel) {
    FloconApp.instance?.client?.filesPlugin?.register(folder)
}

fun floconRegisterFolder(displayName: String, absolutePath: String) {
    floconRegisterFolder(
        FloconFolderModel(
            displayName = displayName,
            absolutePath = absolutePath,
        )
    )
}

interface FloconFilesPlugin {
    fun register(folder: FloconFolderModel)
}
