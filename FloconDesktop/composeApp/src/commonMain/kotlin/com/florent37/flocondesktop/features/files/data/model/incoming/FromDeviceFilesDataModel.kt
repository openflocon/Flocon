package com.florent37.flocondesktop.features.files.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
data class FromDeviceFilesDataModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
)
