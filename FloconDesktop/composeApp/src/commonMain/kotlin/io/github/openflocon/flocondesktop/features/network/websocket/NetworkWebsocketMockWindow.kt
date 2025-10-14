@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.websocket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkWebsocketMockWindow(
    onCloseRequest: () -> Unit,
) {
    FloconDialog(
        onDismissRequest = onCloseRequest,
    ) {
        NetworkWebsocketMockContent(
            onCloseRequest = onCloseRequest,
        )
    }
}

@Composable
private fun NetworkWebsocketMockContent(
    onCloseRequest: () -> Unit,
) {
    val viewModel: NetworkWebsocketMockViewModel = koinViewModel()
    val ids by viewModel.clientsIds.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedClientId.collectAsStateWithLifecycle()

    NetworkWebsocketMockContent(
        selectedId = selectedId,
        ids = ids,
        onClientSelected = viewModel::onClientSelected,
        onSend = viewModel::send,
    )
}

@Composable
private fun NetworkWebsocketMockContent(
    selectedId: String?,
    ids: List<NetworkWebsocketId>,
    onClientSelected: (String) -> Unit,
    onSend: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var selectionExpanded by remember { mutableStateOf(false) }
        FloconExposedDropdownMenuBox(
            expanded = selectionExpanded,
            onExpandedChange = { selectionExpanded = false },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                    selectionExpanded = true
                },
                text = selectedId ?: ids.firstOrNull() ?: "",
                style = FloconTheme.typography.bodySmall,
            )

            FloconExposedDropdownMenu(
                expanded = selectionExpanded,
                onDismissRequest = { selectionExpanded = false },
                modifier = Modifier.width(300.dp)
            ) {
                ids.fastForEach { id ->
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                            onClientSelected(id)
                        },
                        text = id,
                        style = FloconTheme.typography.bodySmall,
                    )
                }
            }
        }

        var message by remember { mutableStateOf("") }
        FloconTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        )

        FloconButton(
            onClick = { onSend(message) },
            modifier = Modifier,
        ) {
            Text("send")
        }
    }
}
