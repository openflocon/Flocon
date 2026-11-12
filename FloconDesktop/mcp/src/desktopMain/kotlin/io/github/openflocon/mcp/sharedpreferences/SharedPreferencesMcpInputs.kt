package io.github.openflocon.mcp.sharedpreferences

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

internal const val DEFAULT_LIMIT = 100
internal const val MAX_LIMIT = 1000

@Serializable
internal data class ListSharedPreferencesInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class GetSharedPreferenceValuesInput(
    @McpDescription("The id of the SharedPreferences file (see list_shared_preferences).")
    val sharedPreferenceId: String,
    @McpDescription("Only the entries containing this text (case insensitive) in their key or value.")
    val text: String? = null,
    @McpDescription("Maximum number of entries returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class SetSharedPreferenceValueInput(
    @McpDescription("The id of the SharedPreferences file (see list_shared_preferences).")
    val sharedPreferenceId: String,
    @McpDescription("The key to edit: it must already exist (see get_shared_preference_values).")
    val key: String,
    @McpDescription(
        "The new value: a JSON string, number or boolean, or an array of strings for a string_set. " +
            "It must be exactly of the type, ex: 12 or \"12\" for an int, true or \"true\" for a boolean.",
    )
    val value: JsonElement,
    @McpDescription(
        "Type of the value: string, int, long, float, boolean or string_set. Default: the type of the current value.",
    )
    val type: String? = null,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
