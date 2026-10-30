package io.github.openflocon.mcp.webview

import io.github.openflocon.mcp.DEVICE_ID_DESCRIPTION
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetArguments
import io.github.openflocon.mcp.PACKAGE_NAME_DESCRIPTION
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal const val DEFAULT_LIMIT = 50
internal const val MAX_LIMIT = 500

@Serializable
internal enum class McpWebViewKind {
    @SerialName("console")
    Console,

    @SerialName("page")
    Page,

    @SerialName("bridge")
    Bridge,
}

@Serializable
internal data class ListWebViewEventsInput(
    @McpDescription("Only this kind of event: console (JS console messages), page (loads, navigation, errors) or bridge (calls from the page to a native object). All kinds by default.")
    val kind: McpWebViewKind? = null,
    @McpDescription("Only the events containing this text in their level / type, message, url or WebView id.")
    val text: String? = null,
    @McpDescription("Only the events after this epoch timestamp, in milliseconds.")
    val sinceTimestamp: Long? = null,
    @McpDescription("Sort order on the event time. Default newest_first.")
    val order: McpOrder = McpOrder.NewestFirst,
    @McpDescription("Maximum number of events returned, from 1 to $MAX_LIMIT. Default $DEFAULT_LIMIT.")
    val limit: Int = DEFAULT_LIMIT,
    @McpDescription(DEVICE_ID_DESCRIPTION)
    override val deviceId: String? = null,
    @McpDescription(PACKAGE_NAME_DESCRIPTION)
    override val packageName: String? = null,
) : McpTargetArguments
