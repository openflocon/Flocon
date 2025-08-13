package com.flocon.data.remote.files.models

import io.github.openflocon.domain.files.models.FromDeviceFilesResultDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class FromDeviceFilesResultDataModel(
    val requestId: String,
    val files: List<FromDeviceFilesDataModel>,
)

internal fun FromDeviceFilesResultDataModel.toDomain() = FromDeviceFilesResultDomainModel(
    requestId = requestId,
    files = files.map(FromDeviceFilesDataModel::toDomain)
)
