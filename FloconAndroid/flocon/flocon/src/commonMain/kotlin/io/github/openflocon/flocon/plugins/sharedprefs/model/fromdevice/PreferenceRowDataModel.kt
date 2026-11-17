package io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal data class PreferenceRowDataModel(
    val key: String,
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
)