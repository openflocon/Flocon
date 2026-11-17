package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetSharedPrefsMessage(
    val requestId: String,
)