package io.github.openflocon.flocon.plugins.files.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class FileDataModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
)