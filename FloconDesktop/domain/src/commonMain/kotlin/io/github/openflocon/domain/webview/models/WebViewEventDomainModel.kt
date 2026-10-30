package io.github.openflocon.domain.webview.models

enum class WebViewEventKind {
    Console,
    Page,
    Bridge,
}

/**
 * Something that happened in a WebView of the app: a console message, a page event or a call to a native bridge.
 *
 * [label] is the console level, the page event type, or `Interface.method` for a bridge call.
 * [message] is the console message, the error description, or the arguments of a bridge call.
 */
data class WebViewEventDomainModel(
    val id: String,
    val webViewId: String,
    val kind: WebViewEventKind,
    val url: String?,
    val label: String,
    val message: String?,
    val sourceId: String?,
    val lineNumber: Int?,
    val statusCode: Int?,
    val result: String?,
    val timestamp: Long,
)
