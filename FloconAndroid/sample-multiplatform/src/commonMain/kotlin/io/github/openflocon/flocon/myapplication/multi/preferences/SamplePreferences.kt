package io.github.openflocon.flocon.myapplication.multi.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue

/**
 * An in-memory preferences store, to test the preferences plugin on every platform.
 */
object SamplePreferences : FloconPreference {
    override val name: String = "Sample preferences"

    // free value, any storage name can be displayed by the desktop app
    override val type: String = "In memory"

    /** Backed by a Compose state, so the screen follows the edits made from the desktop app. */
    var counter: Int by mutableIntStateOf(0)

    private val values = mutableMapOf<String, FloconPreferenceValue>()

    // default keys deleted from the desktop app, until they are set again
    private val removed = mutableSetOf<String>()

    override suspend fun set(columnName: String, value: FloconPreferenceValue) {
        if (columnName == COUNTER) value.intValue?.let { counter = it }
        values[columnName] = value
        removed -= columnName
    }

    override suspend fun remove(columnName: String) {
        if (columnName == COUNTER) counter = 0
        values -= columnName
        removed += columnName
    }

    override suspend fun columns(): List<String> =
        (listOf(COUNTER, "username") + values.keys).distinct() - removed

    override suspend fun get(columnName: String): FloconPreferenceValue? = when (columnName) {
        COUNTER -> FloconPreferenceValue(intValue = counter)
        "username" -> values[columnName] ?: FloconPreferenceValue(stringValue = "flocon")
        else -> values[columnName]
    }

    private const val COUNTER = "counter"
}
