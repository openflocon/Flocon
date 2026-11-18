package com.flocon.data.remote.dashboard.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceCheckBoxValueChangedMessage(
    val id: String,
    val value: Boolean,
)
