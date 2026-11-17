package io.github.openflocon.flocon.plugins.files.model.fromdevice

import kotlinx.serialization.Serializable

/**
 * A root folder of the Files plugin, as shown in the desktop app.
 *
 * @property id stable slug for a default folder, absolute path for a registered one.
 * The desktop sends it back as the path of `listFiles` and `deleteFolderContent`, with `isConstantPath = true`.
 */
@Serializable
internal data class FileRootDataModel(
    val id: String,
    val name: String,
    val path: String,
)
