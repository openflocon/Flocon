package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
import platform.Foundation.NSUserDefaults

// the app UserDefaults are exposed as one preference, others can be added with FloconPreferencesPlugin.register
internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource =
    object : FloconPreferencesDataSource {
        override fun detectLocalPreferences(): List<FloconPreference> =
            listOf(UserDefaultsPreference(NSUserDefaults.standardUserDefaults))
    }

private class UserDefaultsPreference(
    private val defaults: NSUserDefaults,
) : FloconPreference {

    override val name: String = "UserDefaults"

    private fun app(): Map<String, Any?> = defaults.dictionaryRepresentation()
        .mapNotNull { (key, value) -> (key as? String)?.let { it to value } }
        .toMap()

    override suspend fun columns(): List<String> = app().keys.sorted()

    override suspend fun get(columnName: String): FloconPreferenceValue? {
        return when (val value = defaults.objectForKey(columnName)) {
            null -> null
            is String -> FloconPreferenceValue(stringValue = value)
            is Boolean -> FloconPreferenceValue(booleanValue = value)
            is Int -> FloconPreferenceValue(intValue = value)
            is Long -> FloconPreferenceValue(longValue = value)
            is Float -> FloconPreferenceValue(floatValue = value)
            is Double -> FloconPreferenceValue(floatValue = value.toFloat())
            is List<*> -> FloconPreferenceValue(setStringValue = value.mapNotNull { it as? String }.toSet())
            else -> FloconPreferenceValue(stringValue = value.toString())
        }
    }

    override suspend fun set(columnName: String, value: FloconPreferenceValue) {
        val newValue: Any = value.stringValue
            ?: value.booleanValue
            ?: value.intValue
            ?: value.longValue
            ?: value.floatValue
            ?: value.setStringValue?.toList()
            ?: return
        defaults.setObject(newValue, forKey = columnName)
    }
}
