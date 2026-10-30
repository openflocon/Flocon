package io.github.openflocon.flocondesktop.features.webview.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.webview_filter_bridge
import flocondesktop.composeapp.generated.resources.webview_filter_console
import flocondesktop.composeapp.generated.resources.webview_filter_pages
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventKindUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventUiModel
import io.github.openflocon.flocondesktop.features.webview.model.previewWebViewEventUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WebViewEventRow(
    event: WebViewEventUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(
            horizontal = FloconTheme.spacing.medium,
            vertical = FloconTheme.spacing.small,
        ),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = event.label,
                style = FloconTheme.typography.listItem,
                color = if (event.isError) FloconTheme.colors.statusError else FloconTheme.colors.ink,
            )
            Text(
                text = stringResource(event.kind.titleRes()),
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.ink.copy(alpha = 0.6f),
            )
            Text(
                text = event.webViewId,
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.ink.copy(alpha = 0.6f),
            )
            Text(
                text = event.dateFormatted,
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.ink.copy(alpha = 0.6f),
            )
        }
        event.message?.let {
            Text(
                text = it,
                style = FloconTheme.typography.listItemMono,
                color = FloconTheme.colors.ink,
            )
        }
        event.details?.let {
            Text(
                text = it,
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.ink.copy(alpha = 0.6f),
            )
        }
    }
}

private fun WebViewEventKindUiModel.titleRes() = when (this) {
    WebViewEventKindUiModel.Console -> Res.string.webview_filter_console
    WebViewEventKindUiModel.Page -> Res.string.webview_filter_pages
    WebViewEventKindUiModel.Bridge -> Res.string.webview_filter_bridge
}

@Preview
@Composable
private fun WebViewEventRowPreview() {
    FloconTheme {
        WebViewEventRow(event = previewWebViewEventUiModel(isError = true))
    }
}
