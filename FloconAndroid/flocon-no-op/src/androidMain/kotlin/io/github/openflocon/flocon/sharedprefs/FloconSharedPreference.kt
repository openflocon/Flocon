package io.github.openflocon.flocon.sharedprefs

import android.content.SharedPreferences
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue

data class FloconSharedPreference(
    override val name: String,
    val sharedPreferences: SharedPreferences,
) : FloconPreference {

    override suspend fun set(
        rowName: String,
        value: FloconPreferenceValue
    ) {
        // no op
    }

    override suspend fun columns(): List<String> {
        return emptyList() // no op
    }

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        return null // no op
    }
}