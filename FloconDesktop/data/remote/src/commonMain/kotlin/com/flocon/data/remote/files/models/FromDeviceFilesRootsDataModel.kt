package com.flocon.data.remote.files.models

import io.github.openflocon.domain.files.models.FileRootDomainModel
import io.github.openflocon.domain.files.models.FromDeviceFilesRootsDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class FromDeviceFilesRootsDataModel(
    val requestId: String,
    val roots: List<Root>,
) {
    @Serializable
    data class Root(
        val id: String,
        val name: String,
        val path: String,
    )
}

internal fun FromDeviceFilesRootsDataModel.toDomain() = FromDeviceFilesRootsDomainModel(
    requestId = requestId,
    roots = roots.map {
        FileRootDomainModel(
            id = it.id,
            name = it.name,
            path = it.path,
        )
    },
)
