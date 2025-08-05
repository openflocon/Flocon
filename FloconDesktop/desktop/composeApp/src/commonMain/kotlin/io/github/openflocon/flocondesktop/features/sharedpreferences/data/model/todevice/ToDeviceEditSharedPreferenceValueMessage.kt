package io.github.openflocon.flocondesktop.features.sharedpreferences.data.model.todevice

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceEditSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
    val key: String,
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: List<String>? = null,
)
