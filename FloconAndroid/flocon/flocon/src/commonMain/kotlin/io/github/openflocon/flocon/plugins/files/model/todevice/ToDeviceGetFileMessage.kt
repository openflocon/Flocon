package io.github.openflocon.flocon.plugins.files.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetFileMessage(
    val requestId: String,
    val path: String,
)