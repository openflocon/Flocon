package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceSubmittedTextFieldMessage(
    val id: String,
    val value: String,
)