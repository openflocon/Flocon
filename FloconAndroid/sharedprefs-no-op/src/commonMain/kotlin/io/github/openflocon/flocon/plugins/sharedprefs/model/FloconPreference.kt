package io.github.openflocon.flocon.plugins.sharedprefs.model

interface FloconPreference {
    val name: String

    /**
     * Storage behind this preference, displayed next to its name in the desktop app.
     * Use one of [FloconPreferenceType] or any free value ("MMKV", "Realm"...).
     */
    val type: String get() = FloconPreferenceType.Custom

    suspend fun set(
        columnName: String,
        value: FloconPreferenceValue,
    )

    suspend fun columns(): List<String>

    suspend fun get(
        columnName: String,
    ): FloconPreferenceValue?
}

data class FloconPreferenceValue(
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
)

/**
 * Suggested values for [FloconPreference.type]. Any other string is accepted.
 */
object FloconPreferenceType {
    // Android
    const val SharedPreferences = "SharedPreferences"

    // Android, iOS, JVM
    const val DataStore = "DataStore"

    // iOS
    const val UserDefaults = "UserDefaults"
    const val Keychain = "Keychain"

    // JVM
    const val JavaPreferences = "Java Preferences"

    // Wasm
    const val LocalStorage = "LocalStorage"

    // default value
    const val Custom = "Custom"
}
