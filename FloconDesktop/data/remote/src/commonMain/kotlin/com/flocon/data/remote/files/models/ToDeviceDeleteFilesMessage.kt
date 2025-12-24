package com.flocon.data.remote.files.models

import kotlinx.serialization.Serializable

@Serializable
data class ToDeviceDeleteFilesMessage(
    val requestId: String,
    val parentPath: String,
    val filePaths: List<String>,
    val isConstantParentPath: Boolean, // ex: context.files / context.caches
)