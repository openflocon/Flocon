package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCheckbox

@Composable
internal fun DashboardCheckBox(
    rowItem: DashboardContainerViewState.RowItem.CheckBox,
    value: Boolean,
    onCheckedChange: (value: Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconCheckbox(
                checked = value,
                onCheckedChange = onCheckedChange,
                uncheckedColor = FloconTheme.colors.lineStrong
            )
            Text(
                modifier = Modifier.weight(1f),
                style = FloconTheme.typography.bodyMedium.copy(
                    color = FloconTheme.colors.ink,
                ),
                text = rowItem.label,
            )
        }
    }
}

@Preview
@Composable
internal fun DashboardCheckBoxPreview() {
    val rowItem = DashboardContainerViewState.RowItem.CheckBox(
        id = "button1",
        label = "Click Me",
        value = true,
    )
    FloconTheme {
        DashboardCheckBox(
            modifier = Modifier.background(FloconTheme.colors.bg100),
            rowItem = rowItem,
            value = rowItem.value,
            onCheckedChange = {},
        )
    }
}
