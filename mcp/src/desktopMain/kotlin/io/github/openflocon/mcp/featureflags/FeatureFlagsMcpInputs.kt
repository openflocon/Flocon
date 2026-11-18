package io.github.openflocon.mcp.featureflags

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val FEATURE_FLAGS_DEFAULT_LIMIT = 100
internal const val FEATURE_FLAGS_MAX_LIMIT = 500

@Serializable
internal data class ListFeatureFlagsInput(
    @McpDescription("Only the flags of this provider (e.g. \"firebase\"). Default: all the providers.")
    val provider: String? = null,
    @McpDescription("Only the flags whose name, variant or payload contains this text (case insensitive).")
    val query: String? = null,
    @McpDescription("Maximum number of flags returned, from 1 to $FEATURE_FLAGS_MAX_LIMIT. Default $FEATURE_FLAGS_DEFAULT_LIMIT.")
    val limit: Int = FEATURE_FLAGS_DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class GetFeatureFlagInput(
    @McpDescription("The provider of the flag (see list_feature_flags).")
    val provider: String,
    @McpDescription("The name of the flag.")
    val name: String,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
