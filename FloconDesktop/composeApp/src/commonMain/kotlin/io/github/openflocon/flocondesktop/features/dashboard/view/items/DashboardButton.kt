package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DashboardButton(
    onClickButton: (String) -> Unit,
    rowItem: DashboardContainerViewState.RowItem.Button,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(FloconTheme.colors.ink)
                .clickable(onClick = {
                    onClickButton(rowItem.id)
                })
                .padding(all = FloconTheme.spacing.small),
        ) {
            Text(
                text = rowItem.text,
                color = FloconTheme.colors.bg100,
                style = FloconTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
internal fun DashboardButtonPreview() {
    val rowItem = DashboardContainerViewState.RowItem.Button(
        id = "button1",
        text = "Click Me",
    )
    FloconTheme {
        DashboardButton(
            modifier = Modifier.background(FloconTheme.colors.bg100),
            onClickButton = {},
            rowItem = rowItem,
        )
    }
}
