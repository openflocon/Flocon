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
