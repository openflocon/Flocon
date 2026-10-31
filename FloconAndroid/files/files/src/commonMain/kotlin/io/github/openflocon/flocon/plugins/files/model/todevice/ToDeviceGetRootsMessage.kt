package io.github.openflocon.flocon.plugins.files.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetRootsMessage(
    val requestId: String,
)
