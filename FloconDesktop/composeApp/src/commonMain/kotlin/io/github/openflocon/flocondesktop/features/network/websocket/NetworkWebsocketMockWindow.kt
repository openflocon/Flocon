@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.network.websocket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox
import io.github.openflocon.library.designsystem.components.FloconIcon
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
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        FloconDialogHeader(
            title = "Network Websocket Mock",
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            var selectionExpanded by remember { mutableStateOf(false) }

            Text(
                modifier = Modifier
                    .clickable {
                        selectionExpanded = true
                    },
                text = "Websocket : ",
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            FloconExposedDropdownMenuBox(
                expanded = selectionExpanded,
                onExpandedChange = { selectionExpanded = false },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary)
                        .padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                            selectionExpanded = true
                        },
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = selectedId ?: ids.firstOrNull() ?: "",
                        style = FloconTheme.typography.bodySmall,
                    )

                    Image(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "",
                        modifier = Modifier.width(16.dp),
                        colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
                    )
                }

                FloconExposedDropdownMenu(
                    expanded = selectionExpanded,
                    onDismissRequest = { selectionExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    ids.fastForEach { id ->
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable {
                                    onClientSelected(id)
                                    selectionExpanded = false
                                },
                            text = id,
                            style = FloconTheme.typography.bodySmall,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier
                    .clickable {
                        selectionExpanded = true
                    },
                text = "Message : ",
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            var message by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .onKeyEvent { keyEvent ->
                        // detect CMD + Enter
                        if (keyEvent.type == KeyEventType.KeyDown
                            && keyEvent.key == androidx.compose.ui.input.key.Key.Enter
                        ) {
                            onSend(message)

                            // Return 'true' to indicate that the event was consumed
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FloconTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = {
                        Text(
                            "message to send to the websocket",
                            style = FloconTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                Box(
                    modifier = Modifier.size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FloconTheme.colorPalette.tertiary)
                        .clickable {
                            onSend(message)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    FloconIcon(
                        modifier = Modifier.size(14.dp),
                        imageVector = Icons.Filled.Send,
                        tint = FloconTheme.colorPalette.onTertiary,
                    )
                }
            }
        }

    }
}
