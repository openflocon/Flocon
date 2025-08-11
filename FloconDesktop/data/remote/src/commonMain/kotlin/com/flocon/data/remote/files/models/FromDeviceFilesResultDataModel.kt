package com.flocon.data.remote.files.models

import kotlinx.serialization.Serializable

@Serializable
data class FromDeviceFilesResultDataModel(
    val requestId: String,
    val files: List<FromDeviceFilesDataModel>,
)
