package com.florent37.flocondesktop.features.files.data.model.todevice

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceDeleteFileMessage(
    val requestId: String,
    val parentPath: String,
    val filePath: String,
    val isConstantParentPath: Boolean, // ex: context.files / context.caches
)
