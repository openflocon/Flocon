package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialogHeader

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
        FloconDialogHeader(
            title = "Network bad quality",
            modifier = Modifier.fillMaxWidth(),
            trailingContent = {
                FloconButton(
                    onClick = onAddItemClicked,
                    content = {
                        Text("Create")
                    }
                )
            },
        )
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
