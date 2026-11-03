package io.github.openflocon.mcp.screen

import io.github.openflocon.mcp.McpDescription
import kotlinx.serialization.Serializable

@Serializable
internal data class NavigateDesktopAppInput(
    @McpDescription(
        "The screen to display in Flocon Desktop, in snake_case: dashboard, network, images, database, files, " +
            "shared_preferences, analytics, feature_flags, accessibility, tables, deeplinks, adb_commander, " +
            "crash_reporter, timeline, web_view. Unchanged when omitted.",
    )
    val screen: String? = null,
    @McpDescription(
        "Selects this device in Flocon Desktop (see list_devices). Unlike the other tools, this is not the device the call targets: " +
            "it changes what the user has selected in the app.",
    )
    val deviceId: String? = null,
    @McpDescription(
        "Selects this app in Flocon Desktop (see list_devices), and its device if it is another one. Unlike the other tools, " +
            "this is not the app the call targets: it changes what the user has selected in the app.",
    )
    val packageName: String? = null,
    @McpDescription(
        "How the timeline displays the events: \"graph\" or \"list\". Saved in the preferences of the user. Unchanged when omitted.",
    )
    val timelineView: String? = null,
)
