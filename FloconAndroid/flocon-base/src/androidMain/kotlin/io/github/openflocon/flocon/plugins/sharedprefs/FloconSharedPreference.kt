package io.github.openflocon.flocon.plugins.sharedprefs

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
        val originalValue: Any? = sharedPreferences.all[rowName]
        val editor = sharedPreferences.edit()

        if (originalValue is Boolean && value.booleanValue != null) {
            editor.putBoolean(rowName, value.booleanValue)
        } else if (originalValue is Long && value.longValue != null) {
            editor.putLong(rowName, value.longValue)
        } else if (originalValue is Int && value.intValue != null) {
            editor.putInt(rowName, value.intValue)
        } else if (originalValue is Float && value.floatValue != null) {
            editor.putFloat(rowName, value.floatValue)
        } else if (originalValue is String && value.stringValue != null) {
            editor.putString(rowName, value.stringValue)
        } else {
            throw IllegalArgumentException("Method not supported: " + rowName)
        }

        editor.apply()
    }

    override suspend fun columns(): List<String> {
        return sharedPreferences.all.keys.toList()
    }

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        return when (val value = sharedPreferences.all[columnName]) {
            is String -> FloconPreferenceValue(
                stringValue = value,
            )

            is Int -> FloconPreferenceValue(
                intValue = value
            )

            is Boolean -> FloconPreferenceValue(
                booleanValue = value
            )

            is Float -> FloconPreferenceValue(
                floatValue = value
            )

            is Long -> FloconPreferenceValue(
                longValue = value
            )

            is Set<*> -> FloconPreferenceValue(
                setStringValue = value
                    .map { it.toString() }
                    .toSet()
            )

            else -> null // Ne mappe pas les types non reconnus ou null
        }
    }
}