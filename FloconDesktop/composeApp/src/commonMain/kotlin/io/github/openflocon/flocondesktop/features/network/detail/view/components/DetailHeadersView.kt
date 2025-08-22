package io.github.openflocon.flocondesktop.features.network.detail.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DetailHeadersView(
    headers: List<NetworkDetailHeaderUi>,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        headers.fastForEachIndexed { index, item ->
            FloconLineDescription(
                label = item.name,
                value = item.value,
                labelWidth = labelWidth,
                modifier = Modifier.fillMaxWidth()
            )
            if (index != headers.lastIndex) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview
@Composable
private fun DetailHeadersViewPreview() {
    FloconTheme {
        DetailHeadersView(
            headers = listOf(
                previewNetworkDetailHeaderUi()
            ),
            labelWidth = 100.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
