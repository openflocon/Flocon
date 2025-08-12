package com.flocon.data.remote.sharedpreference.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
)
