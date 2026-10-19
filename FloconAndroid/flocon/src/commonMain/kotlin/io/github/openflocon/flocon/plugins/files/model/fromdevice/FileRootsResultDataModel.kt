package io.github.openflocon.flocon.plugins.files.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class FileRootsResultDataModel(
    val requestId: String,
    val roots: List<FileRootDataModel>,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString(this)
    }
}
