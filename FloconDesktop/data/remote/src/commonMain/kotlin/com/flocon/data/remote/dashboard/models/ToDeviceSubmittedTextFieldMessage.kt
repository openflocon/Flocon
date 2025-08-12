package com.flocon.data.remote.dashboard.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceSubmittedTextFieldMessage(
    val id: String,
    val value: String,
)
