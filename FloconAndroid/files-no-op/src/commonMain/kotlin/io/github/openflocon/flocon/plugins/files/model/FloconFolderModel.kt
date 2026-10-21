package io.github.openflocon.flocon.plugins.files.model

/**
 * A folder shown at the top of the Files section of the Flocon desktop app.
 *
 * @property displayName name shown in the desktop app.
 * @property absolutePath location of the folder.
 */
data class FloconFolderModel(
    val displayName: String,
    val absolutePath: String,
)
