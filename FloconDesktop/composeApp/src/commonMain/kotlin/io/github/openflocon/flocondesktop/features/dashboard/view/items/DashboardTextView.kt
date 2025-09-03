package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardItemViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DashboardTextView(
    modifier: Modifier = Modifier,
    rowItem: DashboardItemViewState.RowItem.Text,
) {
    SelectionContainer(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                rowItem.label,
                modifier = Modifier.padding(start = 4.dp),
                color = FloconTheme.colorPalette.onSurface,
                style = FloconTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Thin,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                        ).padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        style = FloconTheme.typography.bodySmall.copy(
                            color = rowItem.color ?: FloconTheme.colorPalette.onSurface,
                        ),
                        text = rowItem.value,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DashboardTextViewPreview() {
    val rowItem = DashboardItemViewState.RowItem.Text(
        label = "label",
        value = "value",
        color = null,
    )
    FloconTheme {
        DashboardTextView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            rowItem = rowItem,
        )
    }
}

@Preview
@Composable
internal fun DashboardTextViewPreview_Red() {
    val rowItem = DashboardItemViewState.RowItem.Text(
        label = "label",
        value = "value",
        color = Color.Red,
    )
    FloconTheme {
        DashboardTextView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.onPrimary,
            ),
            rowItem = rowItem,
        )
    }
}
