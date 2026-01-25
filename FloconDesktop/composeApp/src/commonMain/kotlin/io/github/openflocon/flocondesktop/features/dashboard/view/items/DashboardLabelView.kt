package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DashboardLabelView(
    modifier: Modifier = Modifier,
    rowItem: DashboardContainerViewState.RowItem.Label,
) {
    SelectionContainer(
        modifier = modifier,
    ) {
        Text(
            text = rowItem.label,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = FloconTheme.colorPalette.onPrimary,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Thin,
            ),
        )
    }
}

@Preview
@Composable
internal fun DashboardLabelPreview() {
    val rowItem = DashboardContainerViewState.RowItem.Label(
        label = "label",
        color = null,
    )
    FloconTheme {
        DashboardLabelView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            rowItem = rowItem,
        )
    }
}

@Preview
@Composable
internal fun DashboardLabelViewPreview_Red() {
    val rowItem = DashboardContainerViewState.RowItem.Label(
        label = "label",
        color = Color.Red,
    )
    FloconTheme {
        DashboardLabelView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.onPrimary,
            ),
            rowItem = rowItem,
        )
    }
}
