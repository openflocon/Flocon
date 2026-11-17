package io.github.openflocon.flocon.plugins.files.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class FilesResultDataModel(
    val requestId: String,
    val files: List<FileDataModel>,
)