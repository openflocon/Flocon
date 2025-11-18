package io.github.openflocon.flocon.plugins.sharedprefs.model

interface FloconPreference {
    val name: String

    suspend fun set(
        rowName: String,
        value: FloconPreferenceValue,
    )

    suspend fun columns(): List<String>

    suspend fun get(
        columnName: String,
    ) : FloconPreferenceValue?
}

data class FloconPreferenceValue(
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
)
