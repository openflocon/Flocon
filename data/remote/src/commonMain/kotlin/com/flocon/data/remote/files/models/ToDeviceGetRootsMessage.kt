package com.flocon.data.remote.files.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetRootsMessage(
    val requestId: String,
)
