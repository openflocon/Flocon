package io.github.openflocon.flocon.plugins.files.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceDeleteFileMessage(
    val requestId: String,
    val parentPath: String,
    val filePath: String,
    val isConstantParentPath: Boolean, // true: the path is the id of a root folder
)

