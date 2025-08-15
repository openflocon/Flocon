package com.flocon.data.remote.files.models

import io.github.openflocon.domain.files.models.FromDeviceFilesDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class FromDeviceFilesDataModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
)

internal fun FromDeviceFilesDataModel.toDomain() = FromDeviceFilesDomainModel(
    name = name,
    isDirectory = isDirectory,
    path = path,
    size = size,
    lastModified = lastModified
)
