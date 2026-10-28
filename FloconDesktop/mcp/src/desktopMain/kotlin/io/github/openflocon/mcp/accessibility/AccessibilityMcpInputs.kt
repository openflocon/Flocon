package io.github.openflocon.mcp.accessibility

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.Serializable

internal const val ACCESSIBILITY_DEFAULT_LIMIT = 50
internal const val ACCESSIBILITY_MAX_LIMIT = 500

@Serializable
internal data class ListAccessibilityEventsInput(
    @McpDescription("Only the events of this type, case insensitive, ex: \"VIEW_FOCUSED\" or \"ANNOUNCEMENT\" (the available types are returned by the tool).")
    val type: String? = null,
    @McpDescription("Only the events containing this text (case insensitive) in their text, content description, class name or screen.")
    val text: String? = null,
    @McpDescription("Maximum number of events returned, newest first, from 1 to $ACCESSIBILITY_MAX_LIMIT. Default $ACCESSIBILITY_DEFAULT_LIMIT.")
    val limit: Int = ACCESSIBILITY_DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class GetAccessibilityStateInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments

@Serializable
internal data class ClearAccessibilityEventsInput(
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
