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
