package com.florent37.flocondesktop.features.files.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
data class FromDeviceFilesResultDataModel(
    val requestId: String,
    val files: List<FromDeviceFilesDataModel>,
)
