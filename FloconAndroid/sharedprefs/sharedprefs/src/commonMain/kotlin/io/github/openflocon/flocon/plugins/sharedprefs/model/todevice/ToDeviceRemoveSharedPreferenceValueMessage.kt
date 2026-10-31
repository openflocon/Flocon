package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceRemoveSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
    val key: String,
)
