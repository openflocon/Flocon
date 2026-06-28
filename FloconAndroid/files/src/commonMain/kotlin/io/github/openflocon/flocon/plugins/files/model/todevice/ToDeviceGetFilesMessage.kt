package io.github.openflocon.flocon.plugins.files.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetFilesMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
    val withFoldersSize: Boolean = false,
)


