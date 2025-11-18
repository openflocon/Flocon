package io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class SharedPreferenceValueResultDataModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<PreferenceRowDataModel>,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString(this)
    }
}