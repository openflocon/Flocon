package io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.todevice

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
)
