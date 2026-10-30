package io.github.openflocon.mcp.webview

import kotlinx.serialization.Serializable

@Serializable
internal data class WebViewEventsMcpResult(
    val deviceId: String,
    val packageName: String,
    val returned: Int,
    val hasMore: Boolean,
    val events: List<WebViewEventMcpModel>,
)

/**
 * [label] is the console level, the page event type, or `Interface.method` for a bridge call.
 * [message] is the console message, the error description, or the arguments of a bridge call.
 */
@Serializable
internal data class WebViewEventMcpModel(
    val eventId: String,
    val kind: McpWebViewKind,
    val webViewId: String,
    val timestamp: String,
    val timestampMs: Long,
    val label: String,
    val message: String?,
    val url: String?,
    val sourceId: String?,
    val lineNumber: Int?,
    val statusCode: Int?,
    val result: String?,
)
