package io.github.openflocon.flocon.plugins.sharedprefs.model

import android.content.Context
import android.content.SharedPreferences
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class SharedPreferencesDescriptor(
    val name: String,
    val mode: Int,
) {
    fun getSharedPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(name, mode)
    }
}

internal fun listSharedPreferencesDescriptorToJson(items: List<SharedPreferencesDescriptor>) : String {
    return FloconEncoder.json.encodeToString(items)
}