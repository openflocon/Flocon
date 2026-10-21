package io.github.openflocon.mcp.sharedpreferences

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

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
