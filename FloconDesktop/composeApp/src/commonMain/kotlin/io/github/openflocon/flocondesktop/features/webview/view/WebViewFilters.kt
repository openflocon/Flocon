package io.github.openflocon.flocondesktop.features.webview.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.webview_filter_all
import flocondesktop.composeapp.generated.resources.webview_filter_bridge
import flocondesktop.composeapp.generated.resources.webview_filter_console
import flocondesktop.composeapp.generated.resources.webview_filter_pages
import io.github.openflocon.flocondesktop.features.webview.model.WebViewFilterUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/**
 * One filter at a time: All, or a single kind of event
 */
@Composable
internal fun WebViewFilters(
    selected: WebViewFilterUiModel,
    onFilterSelected: (WebViewFilterUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WebViewFilterUiModel.entries.forEach { filter ->
            WebViewFilterChip(
                filter = filter,
                isSelected = filter == selected,
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}

@Composable
private fun WebViewFilterChip(
    filter: WebViewFilterUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    Text(
        text = stringResource(
            when (filter) {
                WebViewFilterUiModel.All -> Res.string.webview_filter_all
                WebViewFilterUiModel.Console -> Res.string.webview_filter_console
                WebViewFilterUiModel.Page -> Res.string.webview_filter_pages
                WebViewFilterUiModel.Bridge -> Res.string.webview_filter_bridge
            }
        ),
        style = FloconTheme.typography.bodySmall,
        color = FloconTheme.colors.ink,
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) FloconTheme.colors.bg200 else Color.Transparent)
            .border(width = 1.dp, color = FloconTheme.colors.bg200, shape = shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
    )
}

@Preview
@Composable
private fun WebViewFiltersPreview() {
    FloconTheme {
        WebViewFilters(
            selected = WebViewFilterUiModel.Console,
            onFilterSelected = {},
            modifier = Modifier.padding(FloconTheme.spacing.small),
        )
    }
}
