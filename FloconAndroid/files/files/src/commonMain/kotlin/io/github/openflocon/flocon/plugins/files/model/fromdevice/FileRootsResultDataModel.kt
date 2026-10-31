package io.github.openflocon.flocon.plugins.files.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class FileRootsResultDataModel(
    val requestId: String,
    val roots: List<FileRootDataModel>,
)
