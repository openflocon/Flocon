package io.github.openflocon.mcp.sharedpreferences

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class SharedPreferencesMcpResult(
    val deviceId: String,
    val packageName: String,
    val sharedPreferences: List<SharedPreferenceMcpModel>,
)

@Serializable
internal data class SharedPreferenceMcpModel(
    val sharedPreferenceId: String,
    val name: String,
    /** storage of the preference (SharedPreferences, DataStore, UserDefaults, Keychain... or a free value), null for older SDKs */
    val type: String?,
)

@Serializable
internal data class SharedPreferenceValuesMcpResult(
    val deviceId: String,
    val packageName: String,
    val sharedPreferenceId: String,
    val total: Int,
    val returned: Int,
    val hasMore: Boolean,
    val entries: List<SharedPreferenceEntryMcpModel>,
)

@Serializable
internal data class SharedPreferenceEntryMcpModel(
    val key: String,
    /** string, int, long, float, boolean or string_set */
    val type: String,
    val value: JsonElement,
)

@Serializable
internal data class SetSharedPreferenceValueMcpResult(
    val deviceId: String,
    val packageName: String,
    val sharedPreferenceId: String,
    val key: String,
    /** string, int, long, float, boolean or string_set */
    val type: String,
    /** value before the edit, to restore it if needed */
    val previousValue: JsonElement,
    /** value read back from the app after the edit, null when the app did not answer in time */
    val newValue: JsonElement?,
    /** false when the app did not answer the edit in time: it may or may not have been applied */
    val confirmed: Boolean,
)
