package io.github.openflocon.flocondesktop.features.network.ui.view.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkDetailHeaderUi
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailHeadersView(
    headers: List<NetworkDetailHeaderUi>,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        headers.fastForEachIndexed { index, item ->
            DetailHeadersItemView(
                state = item,
                labelWidth = labelWidth,
                modifier = Modifier.fillMaxWidth(),
            )
            if (index != headers.lastIndex) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun DetailHeadersItemView(
    state: NetworkDetailHeaderUi,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Row(
            modifier = modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.name,
                style = FloconTheme.typography.bodySmall, // Slightly smaller title for details
                color = FloconTheme.colorPalette.onBackground.copy(alpha = 0.7f), // Muted label color
                modifier = Modifier.width(labelWidth).padding(end = 8.dp),
            )
            Text(
                text = state.value,
                style = FloconTheme.typography.bodySmall, // Body text for the URL
                color = FloconTheme.colorPalette.onBackground, // Primary text color
                modifier = Modifier.weight(1f), // Takes remaining space
            )
        }
    }
}

@Preview
@Composable
private fun DetailHeadersItemViewPreview() {
    FloconTheme {
        DetailHeadersItemView(
            state = previewNetworkDetailHeaderUi(),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
