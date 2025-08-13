package com.flocon.data.remote.files.mapper

import com.flocon.data.remote.files.models.FromDeviceFilesDataModel
import com.flocon.data.remote.files.models.FromDeviceFilesResultDataModel
import io.github.openflocon.domain.files.models.FromDeviceFilesDomainModel
import io.github.openflocon.domain.files.models.FromDeviceFilesResultDomainModel

// TODO INTERNAL
fun FromDeviceFilesResultDataModel.toDomain() = FromDeviceFilesResultDomainModel(
    requestId = requestId,
    files = files.map(FromDeviceFilesDataModel::toDomain),
)

fun FromDeviceFilesDataModel.toDomain() = FromDeviceFilesDomainModel(
    isDirectory = isDirectory,
    lastModified = lastModified,
    name = name,
    size = size,
    path = path,
)
