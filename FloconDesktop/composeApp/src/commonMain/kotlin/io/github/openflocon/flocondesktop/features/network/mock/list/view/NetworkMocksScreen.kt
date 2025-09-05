package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.NetworkEditionWindow
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.model.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkMocksWindow(
    instanceId: String,
    fromNetworkCallId: String?,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NetworkMocksViewModel = koinViewModel()
    LaunchedEffect(viewModel, fromNetworkCallId) {
        viewModel.initWith(fromNetworkCallId)
    }
    val mocks by viewModel.items.collectAsStateWithLifecycle()
    val editionWindow by viewModel.editionWindow.collectAsStateWithLifecycle()
    key(instanceId) {
        FloconDialog(
            onDismissRequest = onCloseRequest,
        ) {
            NetworkMocksContent(
                mocks = mocks,
                modifier = Modifier.fillMaxWidth(),
                onItemClicked = viewModel::clickOnMock,
                onAddItemClicked = viewModel::createNewMock,
                onDeleteClicked = viewModel::deleteMock,
                changeIsEnabled = viewModel::changeIsEnabled,
            )
        }

        editionWindow?.let {
            NetworkEditionWindow(
                instanceId = it.windowInstanceId,
                state = it.selectedMockUiModel,
                onCloseRequest = viewModel::cancelMockCreation,
                onCancel = viewModel::cancelMockCreation,
                onSave = viewModel::addMock,
            )
        }
    }
}

@Composable
private fun NetworkMocksContent(
    mocks: List<MockNetworkLineUiModel>,
    onItemClicked: (id: String) -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    changeIsEnabled: (id: String, enabled: Boolean) -> Unit,
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(8.dp)
    ) {
        FloconDialogHeader(
            title = "Mocks",
            trailingContent = {
                FloconIconTonalButton(
                    onClick = onAddItemClicked,
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Add
                    )
                }
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(mocks) {
                MockLineView(
                    item = it,
                    onClicked = onItemClicked,
                    onDeleteClicked = onDeleteClicked,
                    changeIsEnabled = changeIsEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
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
            onItemClicked = {},
            onDeleteClicked = {},
            onAddItemClicked = {},
            changeIsEnabled = { _, _ -> },
        )
    }
}
