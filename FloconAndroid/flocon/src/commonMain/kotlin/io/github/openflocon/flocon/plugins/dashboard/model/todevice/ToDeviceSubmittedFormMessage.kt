package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceSubmittedFormMessage(
    val id: String,
    val values: Map<String, String>
)
