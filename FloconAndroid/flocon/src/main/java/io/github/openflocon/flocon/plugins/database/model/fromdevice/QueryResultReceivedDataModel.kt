package io.github.openflocon.flocon.plugins.database.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class QueryResultDataModel(
    val requestId: String,
    val result: String,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString<QueryResultDataModel>(this)
    }
}