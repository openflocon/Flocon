package com.florent37.flocondesktop.features.files.data.model.todevice

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceDeleteFolderContentMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
)
