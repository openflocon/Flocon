package com.flocon.data.remote.files.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetFileMessage(
    val requestId: String,
    val path: String,
)
