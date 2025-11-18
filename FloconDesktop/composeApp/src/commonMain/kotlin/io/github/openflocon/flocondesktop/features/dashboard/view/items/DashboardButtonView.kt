package io.github.openflocon.flocondesktop.features.dashboard.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.dashboard.model.DashboardContainerViewState
import io.github.openflocon.library.designsystem.FloconTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun DashboardButtonView(
    onClickButton: (String) -> Unit,
    rowItem: DashboardContainerViewState.RowItem.Button,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(onClick = {
                    onClickButton(rowItem.id)
                })
                .padding(all = 8.dp),
        ) {
            Text(
                text = rowItem.text,
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
internal fun DashboardButtonViewPreview() {
    val rowItem = DashboardContainerViewState.RowItem.Button(
        id = "button1",
        text = "Click Me",
    )
    FloconTheme {
        DashboardButtonView(
            modifier = Modifier.background(FloconTheme.colorPalette.primary),
            onClickButton = {},
            rowItem = rowItem,
        )
    }
}
