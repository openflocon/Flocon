package io.github.openflocon.flocon.plugins.files.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class FilesResultDataModel(
    val requestId: String,
    val files: List<FileDataModel>,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString(this)
    }
}