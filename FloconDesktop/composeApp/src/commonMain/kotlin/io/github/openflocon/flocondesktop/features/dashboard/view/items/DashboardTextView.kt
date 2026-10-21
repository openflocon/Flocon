package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import androidx.compose.desktop.ui.tooling.preview.Preview

@Composable
internal fun DashboardTextView(
    modifier: Modifier = Modifier,
    rowItem: DashboardContainerViewState.RowItem.Text,
) {
    SelectionContainer(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        ) {
            Text(
                text = rowItem.label,
                modifier = Modifier.padding(start = FloconTheme.spacing.extraSmall),
                color = FloconTheme.colors.ink,
                style = FloconTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Thin,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                FloconTextFieldWithoutM3(
                    value = rowItem.value,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = false,
                    containerColor = FloconTheme.colors.bg200,
                    textStyle = FloconTheme.typography.bodySmall,
                    contentColor = rowItem.color ?: FloconTheme.colors.inkMuted,
                    contentPadding = PaddingValues(FloconTheme.spacing.small),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
internal fun DashboardTextViewPreview() {
    val rowItem = DashboardContainerViewState.RowItem.Text(
        label = "label",
        value = "value",
        color = null,
    )
    FloconTheme {
        DashboardTextView(
            modifier = Modifier.background(
                FloconTheme.colors.bg100,
            ),
            rowItem = rowItem,
        )
    }
}

@Preview
@Composable
internal fun DashboardTextViewPreview_Red() {
    val rowItem = DashboardContainerViewState.RowItem.Text(
        label = "label",
        value = "value",
        color = Color.Red,
    )
    FloconTheme {
        DashboardTextView(
            modifier = Modifier.background(
                FloconTheme.colors.ink,
            ),
            rowItem = rowItem,
        )
    }
}
