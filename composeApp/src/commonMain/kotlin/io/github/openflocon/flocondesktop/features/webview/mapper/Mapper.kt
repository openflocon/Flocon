package io.github.openflocon.flocondesktop.features.webview.mapper

import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.webview.models.WebViewEventDomainModel
import io.github.openflocon.domain.webview.models.WebViewEventKind
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventDetailUiModel
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
    kind = kind.toUi(),
    webViewId = webViewId,
    dateFormatted = formatDate(timestamp),
    label = label,
    message = message,
    details = details(),
    isError = isError(),
)

// the SDKs send an empty text (not null) when they have nothing to say, e.g. the reason phrase of an HTTP error
internal fun WebViewEventDomainModel.toDetailUi() = WebViewEventDetailUiModel(
    kind = kind.toUi(),
    label = label,
    message = message?.takeIf { it.isNotBlank() },
    webViewId = webViewId,
    time = formatDate(timestamp),
    url = url?.takeIf { it.isNotBlank() },
    source = sourceId?.takeIf { it.isNotBlank() }?.let { source -> lineNumber?.let { "$source:$it" } ?: source },
    statusCode = statusCode?.toString(),
    result = result?.takeIf { it.isNotBlank() },
    isError = isError(),
)

private fun WebViewEventKind.toUi() = when (this) {
    WebViewEventKind.Console -> WebViewEventKindUiModel.Console
    WebViewEventKind.Page -> WebViewEventKindUiModel.Page
    WebViewEventKind.Bridge -> WebViewEventKindUiModel.Bridge
}

private fun WebViewEventDomainModel.isError() = when (kind) {
    WebViewEventKind.Console -> label == "error"
    WebViewEventKind.Page -> label in ErrorPageEvents
    WebViewEventKind.Bridge -> false
}

private fun WebViewEventDomainModel.details(): String? = listOfNotNull(
    when {
        sourceId != null && lineNumber != null -> "$sourceId:$lineNumber"
        sourceId != null -> sourceId
        else -> url
    },
    statusCode?.let { "HTTP $it" },
    result?.let { "-> $it" },
).takeIf { it.isNotEmpty() }?.joinToString(separator = "  ")
