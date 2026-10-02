package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
)