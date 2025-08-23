package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun NetworkBadQualityContent(
    lines: List<NetworkBadQualityLineUiModel>,
    onItemClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    setEnabled: (id: String?) -> Unit,
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.panel)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = "Bad network configs",
                modifier = Modifier
                    .background(FloconTheme.colorPalette.panel)
                    .padding(all = 12.dp),
                style = FloconTheme.typography.titleMedium,
                color = FloconTheme.colorPalette.onSurface,
            )
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FloconTheme.colorPalette.onSurface)
                    .clickable(onClick = onAddItemClicked)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "Create",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.panel,
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .height(400.dp),
        ) {
            items(lines) {
                BadNetworkLineView(
                    item = it,
                    onClicked = onItemClicked,
                    onDeleteClicked = onDeleteClicked,
                    enableClicked = { id, enabled ->
                        if (enabled) {
                            setEnabled(id)
                        } else {
                            setEnabled(null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
