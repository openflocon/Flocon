package io.github.openflocon.flocondesktop.features.dashboard.ui.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.ui.model.DashboardItemViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DashboardCheckBoxView(
    rowItem: DashboardItemViewState.RowItem.CheckBox,
    onUpdateCheckBox: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember(rowItem.value) {
        mutableStateOf(rowItem.value)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    text = rowItem.label,
                )
            }
            Checkbox(
                checked = value,
                onCheckedChange = {
                    value = it
                    onUpdateCheckBox(rowItem.id, it)
                },
            )
        }
    }
}

@Preview
@Composable
internal fun DashboardCheckBoxViewPreview() {
    val rowItem = DashboardItemViewState.RowItem.CheckBox(
        id = "button1",
        label = "Click Me",
        value = true,
    )
    FloconTheme {
        DashboardCheckBoxView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.panel,
            ),
            rowItem = rowItem,
            onUpdateCheckBox = { _, _ -> },
        )
    }
}
