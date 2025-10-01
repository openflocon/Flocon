package io.github.openflocon.flocondesktop.features.network.mock.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.view.NetworkEditionWindow
import io.github.openflocon.flocondesktop.features.network.mock.list.model.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.mock.list.model.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
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
                changeIsShared = viewModel::changeIsShared,
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
    changeIsShared: (id: String, isShared: Boolean) -> Unit,
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FloconDialogHeader(
            title = "Mocks",
            modifier = Modifier.fillMaxWidth(),
            trailingContent = {
                FloconButton(
                    onClick = onAddItemClicked,
                ) {
                    Text("Create")
                }
            }
        )
        MocksHeaderView(Modifier.fillMaxWidth())
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(400.dp),
        ) {
            items(mocks) {
                MockLineView(
                    item = it,
                    onClicked = onItemClicked,
                    onDeleteClicked = onDeleteClicked,
                    changeIsEnabled = changeIsEnabled,
                    changeIsShared = changeIsShared,
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
            changeIsShared = { _, _ -> },
        )
    }
}
