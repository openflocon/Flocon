package io.github.openflocon.flocondesktop.features.webview.model

import androidx.compose.runtime.Immutable

@Immutable
enum class WebViewFilterUiModel {
    All,
    Console,
    Page,
    Bridge,
}

@Immutable
enum class WebViewEventKindUiModel {
    Console,
    Page,
    Bridge,
}

@Immutable
data class WebViewEventUiModel(
    val id: String,
    val kind: WebViewEventKindUiModel,
    val webViewId: String,
    val dateFormatted: String,
    val label: String,
    val message: String?,
    val details: String?,
    val isError: Boolean,
)

// the panel of an event: everything the row cannot show on its single line
@Immutable
internal data class WebViewEventDetailUiModel(
    val kind: WebViewEventKindUiModel,
    // the console level, the page event type, or `Interface.method` for a bridge call
    val label: String,
    // the console message, the error description, or the arguments of a bridge call
    val message: String?,
    val webViewId: String,
    val time: String,
    val url: String?,
    // `file:line` of a console message
    val source: String?,
    val statusCode: String?,
    // the result of a bridge call
    val result: String?,
    val isError: Boolean,
)

internal fun previewWebViewEventDetailUiModel() = WebViewEventDetailUiModel(
    kind = WebViewEventKindUiModel.Page,
    label = "http_error",
    message = "Not Found",
    webViewId = "checkout",
    time = "12/10/2020 12:00:00.000",
    url = "https://example.com/formulaire-retractation/",
    source = null,
    statusCode = "404",
    result = null,
    isError = true,
)

sealed interface WebViewAction {
    data object Clear : WebViewAction
}

fun previewWebViewEventUiModel(
    kind: WebViewEventKindUiModel = WebViewEventKindUiModel.Console,
    isError: Boolean = false,
) = WebViewEventUiModel(
    id = "id",
    kind = kind,
    webViewId = "checkout",
    dateFormatted = "12/10/2020 12:00:00",
    label = if (isError) "error" else "log",
    message = "Something happened in the page",
    details = "https://example.com/app.js:42",
    isError = isError,
)
