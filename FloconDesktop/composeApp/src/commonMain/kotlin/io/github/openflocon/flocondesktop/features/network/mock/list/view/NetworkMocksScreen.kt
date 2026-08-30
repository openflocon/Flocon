package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.MockEditorScreen
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.model.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconOverflow
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkMocksScreen(
    fromNetworkCallId: String?,
    modifier: Modifier = Modifier,
) {
    val viewModel: NetworkMocksViewModel = koinViewModel()
    LaunchedEffect(viewModel, fromNetworkCallId) {
        viewModel.initWith(fromNetworkCallId)
    }
    val mocks by viewModel.items.collectAsStateWithLifecycle()
    val editionWindow by viewModel.editionWindow.collectAsStateWithLifecycle()

    val currentEdition = editionWindow
    if (currentEdition != null) {
        MockEditorScreen(
            initialMock = currentEdition.selectedMockUiModel,
            onCancel = viewModel::cancelMockCreation,
            onSave = viewModel::addMock,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        NetworkMocksContent(
            mocks = mocks,
            modifier = modifier.fillMaxSize(),
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun NetworkMocksWindow(
    fromNetworkCallId: String?
) {
    NetworkMocksScreen(fromNetworkCallId = fromNetworkCallId)
}

sealed interface NetworkMockAction {
    data class OnItemClicked(val id: String) : NetworkMockAction
    data class OnDeleteClicked(val id: String) : NetworkMockAction
    data class ChangeIsEnabled(val id: String, val enabled: Boolean) : NetworkMockAction
    data class ChangeIsShared(val id: String, val isShared: Boolean) : NetworkMockAction
    data object OnImportClicked : NetworkMockAction
    data object OnExportClicked : NetworkMockAction
    data object OnAddItemClicked : NetworkMockAction
}

@Composable
private fun NetworkMocksContent(
    mocks: List<MockNetworkLineUiModel>,
    onAction: (NetworkMockAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconButton(
                onClick = { onAction(NetworkMockAction.OnAddItemClicked) },
                containerColor = FloconTheme.colorPalette.tertiary,
            ) {
                Text("Create")
            }
            FloconOverflow {
                FloconDropdownMenuItem(
                    text = "Import",
                    leadingIcon = Icons.Outlined.FileDownload,
                    onClick = { onAction(NetworkMockAction.OnImportClicked) }
                )
                FloconDropdownMenuItem(
                    text = "Export",
                    leadingIcon = Icons.Outlined.FileUpload,
                    onClick = { onAction(NetworkMockAction.OnExportClicked) }
                )
            }
        }
        MocksHeaderView(Modifier.fillMaxWidth())

        if (mocks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No mocks configured yet",
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f),
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(
                    items = mocks,
                    key = MockNetworkLineUiModel::id
                ) {
                    MockLineView(
                        item = it,
                        onClicked = { id -> onAction(NetworkMockAction.OnItemClicked(id)) },
                        onDeleteClicked = { id -> onAction(NetworkMockAction.OnDeleteClicked(id)) },
                        changeIsEnabled = { id, enabled ->
                            onAction(
                                NetworkMockAction.ChangeIsEnabled(
                                    id = id,
                                    enabled = enabled
                                )
                            )
                        },
                        changeIsShared = { id, isShared ->
                            onAction(
                                NetworkMockAction.ChangeIsShared(
                                    id = id,
                                    isShared = isShared
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun NetworkMocksContentPreview() {
    FloconTheme {
        NetworkMocksContent(
            mocks = List(10) {
                previewMockNetworkLineUiModel()
            },
            onAction = {},
        )
    }
}
