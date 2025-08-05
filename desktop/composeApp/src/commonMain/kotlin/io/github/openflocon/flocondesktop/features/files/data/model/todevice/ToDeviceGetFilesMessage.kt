package io.github.openflocon.flocondesktop.features.files.data.model.todevice

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetFilesMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
)
