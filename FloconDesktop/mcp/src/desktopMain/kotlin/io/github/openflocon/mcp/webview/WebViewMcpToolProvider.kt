package io.github.openflocon.mcp.webview

import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.domain.webview.repository.WebViewRepository
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import kotlin.time.Instant

internal class WebViewMcpToolProvider(
    private val webViewRepository: WebViewRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_webview_events",
            title = "List WebView events",
            description = "Lists what happened in the WebViews of the mobile app, newest first: JS console messages " +
                "(kind console, label is the level), page events such as loads, navigation and errors " +
                "(kind page), and calls from the page to a native object (kind bridge). " +
                "The requests made by the page are in the network tools.",
            annotations = readOnlyToolAnnotations,
            handler = ::listWebViewEvents,
        )
    }

    private suspend fun listWebViewEvents(input: ListWebViewEventsInput): WebViewEventsMcpResult {
        val target = targetResolver.resolve(input)
        val limit = input.limit.coerceIn(1, MAX_LIMIT)

        val events = webViewRepository.searchEvents(
            deviceIdAndPackageName = target,
            kind = input.kind?.toDomain(),
            sinceTimestamp = input.sinceTimestamp,
            text = input.text?.takeIf { it.isNotBlank() },
            newestFirst = input.order == McpOrder.NewestFirst,
            limit = limit + 1,
        )
        val returned = events.take(limit)

        return WebViewEventsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            returned = returned.size,
            hasMore = events.size > limit,
            events = returned.map { it.toMcp() },
        )
    }
}

private fun McpWebViewKind.toDomain() = when (this) {
    McpWebViewKind.Console -> WebViewEventKind.Console
    McpWebViewKind.Page -> WebViewEventKind.Page
    McpWebViewKind.Bridge -> WebViewEventKind.Bridge
}

private fun WebViewEventKind.toMcp() = when (this) {
    WebViewEventKind.Console -> McpWebViewKind.Console
    WebViewEventKind.Page -> McpWebViewKind.Page
    WebViewEventKind.Bridge -> McpWebViewKind.Bridge
}

private fun WebViewEventDomainModel.toMcp() = WebViewEventMcpModel(
    eventId = id,
    kind = kind.toMcp(),
    webViewId = webViewId,
    timestamp = Instant.fromEpochMilliseconds(timestamp).toString(),
    timestampMs = timestamp,
    label = label,
    message = message,
    url = url,
    sourceId = sourceId,
    lineNumber = lineNumber,
    statusCode = statusCode,
    result = result,
)
