package io.github.openflocon.flocon.plugins.files.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceDeleteFolderContentMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // true: the path is the id of a root folder
)

