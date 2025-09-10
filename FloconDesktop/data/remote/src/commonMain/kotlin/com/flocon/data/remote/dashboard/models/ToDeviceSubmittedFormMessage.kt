package com.flocon.data.remote.dashboard.models

import kotlinx.serialization.Serializable


@Serializable
data class ToDeviceSubmittedFormMessage(
    val id: String,
    val values: Map<String, String>
)
