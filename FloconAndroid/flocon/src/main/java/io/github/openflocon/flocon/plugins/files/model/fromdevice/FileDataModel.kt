package io.github.openflocon.flocon.plugins.files.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class FileDataModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
)