package io.github.openflocon.flocondesktop.features.webview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.webview_detail_copy_message
import flocondesktop.composeapp.generated.resources.webview_detail_copy_result
import flocondesktop.composeapp.generated.resources.webview_detail_details
import flocondesktop.composeapp.generated.resources.webview_detail_message
import flocondesktop.composeapp.generated.resources.webview_detail_result
import flocondesktop.composeapp.generated.resources.webview_detail_source
import flocondesktop.composeapp.generated.resources.webview_detail_status
import flocondesktop.composeapp.generated.resources.webview_detail_time
import flocondesktop.composeapp.generated.resources.webview_detail_url
import flocondesktop.composeapp.generated.resources.webview_detail_webview
import flocondesktop.composeapp.generated.resources.webview_filter_bridge
import flocondesktop.composeapp.generated.resources.webview_filter_console
import flocondesktop.composeapp.generated.resources.webview_filter_pages
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventDetailUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventKindUiModel
import io.github.openflocon.flocondesktop.features.webview.model.previewWebViewEventDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WebViewEventDetailScreen(
    id: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<WebViewEventDetailViewModel>(key = id) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        WebViewEventDetailScreen(
            uiState = it,
            onCopyMessage = viewModel::onCopyMessage,
            onCopyResult = viewModel::onCopyResult,
            modifier = modifier,
        )
    }
}

@Composable
internal fun WebViewEventDetailScreen(
    uiState: WebViewEventDetailUiModel,
    onCopyMessage: () -> Unit,
    onCopyResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val kindTitle = stringResource(uiState.kind.titleRes())
    val labelWebView = stringResource(Res.string.webview_detail_webview)
    val labelUrl = stringResource(Res.string.webview_detail_url)
    val labelSource = stringResource(Res.string.webview_detail_source)
    val labelStatus = stringResource(Res.string.webview_detail_status)
    val labelTime = stringResource(Res.string.webview_detail_time)

    DetailPanel(
        modifier = modifier,
        header = {
            DetailHeader(
                label = kindTitle,
                title = uiState.label,
                chips = {
                    DetailChip(uiState.webViewId)
                    uiState.statusCode?.let { DetailChip("HTTP $it") }
                },
            )
        },
    ) {
        uiState.message?.let { message ->
            DetailSectionCard(
                title = stringResource(Res.string.webview_detail_message),
                badge = null,
                copyContentDescription = stringResource(Res.string.webview_detail_copy_message),
                onCopy = onCopyMessage,
            ) {
                MonoText(
                    text = message,
                    color = if (uiState.isError) FloconTheme.colors.statusError else FloconTheme.colors.ink,
                )
            }
        }
        uiState.result?.let { result ->
            DetailSectionCard(
                title = stringResource(Res.string.webview_detail_result),
                badge = null,
                copyContentDescription = stringResource(Res.string.webview_detail_copy_result),
                onCopy = onCopyResult,
            ) {
                MonoText(text = result, color = FloconTheme.colors.ink)
            }
        }
        DetailSectionCard(
            title = stringResource(Res.string.webview_detail_details),
            badge = null,
            copyContentDescription = "",
            onCopy = null,
        ) {
            DetailKeyValueList(
                entries = listOfNotNull(
                    labelWebView to uiState.webViewId,
                    uiState.url?.let { labelUrl to it },
                    uiState.source?.let { labelSource to it },
                    uiState.statusCode?.let { labelStatus to it },
                    labelTime to uiState.time,
                ),
            )
        }
    }
}

@Composable
private fun MonoText(
    text: String,
    color: Color,
) {
    SelectionContainer {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.medium),
        )
    }
}

private fun WebViewEventKindUiModel.titleRes() = when (this) {
    WebViewEventKindUiModel.Console -> Res.string.webview_filter_console
    WebViewEventKindUiModel.Page -> Res.string.webview_filter_pages
    WebViewEventKindUiModel.Bridge -> Res.string.webview_filter_bridge
}

@Preview
@Composable
private fun WebViewEventDetailScreenPreview() {
    FloconTheme {
        WebViewEventDetailScreen(
            uiState = previewWebViewEventDetailUiModel(),
            onCopyMessage = {},
            onCopyResult = {},
        )
    }
}

@Preview
@Composable
private fun WebViewEventDetailScreenBridgePreview() {
    FloconTheme {
        WebViewEventDetailScreen(
            uiState = previewWebViewEventDetailUiModel().copy(
                kind = WebViewEventKindUiModel.Bridge,
                label = "Android.getToken",
                message = """["checkout"]""",
                result = """{"token":"abc"}""",
                url = null,
                statusCode = null,
                isError = false,
            ),
            onCopyMessage = {},
            onCopyResult = {},
        )
    }
}
