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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun DashboardButton(
    onClickButton: (String) -> Unit,
    rowItem: DashboardContainerViewState.RowItem.Button,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(FloconTheme.colors.accent)
            .clickable(onClick = {
                onClickButton(rowItem.id)
            })
            .padding(all = FloconTheme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rowItem.text,
            color = FloconTheme.colors.accentInk,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
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
