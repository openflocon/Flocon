package com.flocon.data.remote.files.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceGetFilesMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
    val withFoldersSize: Boolean,
)
