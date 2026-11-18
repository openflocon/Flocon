package com.flocon.data.remote.sharedpreference.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceRemoveSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
    val key: String,
)
