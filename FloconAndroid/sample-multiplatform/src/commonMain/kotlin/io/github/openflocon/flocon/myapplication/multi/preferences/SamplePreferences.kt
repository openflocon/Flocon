package io.github.openflocon.flocon.myapplication.multi.preferences

import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue

/**
 * An in-memory preferences store, to test the preferences plugin on every platform.
 */
object SamplePreferences : FloconPreference {
    override val name: String = "Sample preferences"

    var counter: Int = 0

    private val values = mutableMapOf<String, FloconPreferenceValue>()

    override suspend fun set(columnName: String, value: FloconPreferenceValue) {
        if (columnName == COUNTER) value.intValue?.let { counter = it }
        values[columnName] = value
    }

    override suspend fun columns(): List<String> = (listOf(COUNTER, "username") + values.keys).distinct()

    override suspend fun get(columnName: String): FloconPreferenceValue? = when (columnName) {
        COUNTER -> FloconPreferenceValue(intValue = counter)
        "username" -> values[columnName] ?: FloconPreferenceValue(stringValue = "flocon")
        else -> values[columnName]
    }

    private const val COUNTER = "counter"
}
