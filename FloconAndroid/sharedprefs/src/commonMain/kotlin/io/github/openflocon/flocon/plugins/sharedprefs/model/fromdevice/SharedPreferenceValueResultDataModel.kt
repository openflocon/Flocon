package io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class SharedPreferenceValueResultDataModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<PreferenceRowDataModel>,
)