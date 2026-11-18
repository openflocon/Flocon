package io.github.openflocon.mcp.deeplink

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

@Serializable
internal data class ListDeeplinksInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ListDeeplinkHistoryInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class OpenDeeplinkInput(
    @McpDescription("The full deeplink to open on the device, with its parameters filled, ex: myapp://profile/42?tab=posts")
    val link: String,
    @McpDescription("Optional: id of a declared deeplink (from list_deeplinks) this link comes from, to keep its label in the history.")
    val deeplinkId: Long? = null,
    @McpDescription("Whether to add the deeplink to the Flocon history (default true).")
    val saveIntoHistory: Boolean? = null,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
