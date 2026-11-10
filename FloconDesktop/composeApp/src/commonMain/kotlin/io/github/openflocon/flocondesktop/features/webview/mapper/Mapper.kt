package io.github.openflocon.flocondesktop.features.webview.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventKindUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewFilterUiModel

private val ErrorPageEvents = setOf("error", "http_error", "ssl_error")

fun WebViewFilterUiModel.toDomain(): WebViewEventKind? = when (this) {
    WebViewFilterUiModel.All -> null
    WebViewFilterUiModel.Console -> WebViewEventKind.Console
    WebViewFilterUiModel.Page -> WebViewEventKind.Page
    WebViewFilterUiModel.Bridge -> WebViewEventKind.Bridge
}

fun WebViewEventDomainModel.mapToUi() = WebViewEventUiModel(
    id = id,
    kind = when (kind) {
        WebViewEventKind.Console -> WebViewEventKindUiModel.Console
        WebViewEventKind.Page -> WebViewEventKindUiModel.Page
        WebViewEventKind.Bridge -> WebViewEventKindUiModel.Bridge
    },
    webViewId = webViewId,
    dateFormatted = formatDate(timestamp),
    label = label,
    message = message,
    details = details(),
    isError = when (kind) {
        WebViewEventKind.Console -> label == "error"
        WebViewEventKind.Page -> label in ErrorPageEvents
        WebViewEventKind.Bridge -> false
    },
)

private fun WebViewEventDomainModel.details(): String? = listOfNotNull(
    when {
        sourceId != null && lineNumber != null -> "$sourceId:$lineNumber"
        sourceId != null -> sourceId
        else -> url
    },
    statusCode?.let { "HTTP $it" },
    result?.let { "-> $it" },
).takeIf { it.isNotEmpty() }?.joinToString(separator = "  ")
