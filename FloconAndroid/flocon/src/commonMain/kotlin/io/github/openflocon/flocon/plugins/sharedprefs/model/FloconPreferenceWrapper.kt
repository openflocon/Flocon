package io.github.openflocon.flocon.plugins.sharedprefs.model

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class PreferencesDescriptor(
    val name: String,
)

internal fun listSharedPreferencesDescriptorToJson(items: List<FloconPreference>): String {
    val value = items.map { PreferencesDescriptor(it.name) }
    return FloconEncoder.json.encodeToString(value)
}