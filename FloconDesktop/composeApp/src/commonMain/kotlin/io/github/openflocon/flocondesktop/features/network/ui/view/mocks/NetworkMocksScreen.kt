package io.github.openflocon.flocondesktop.features.network.ui.view.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkMocksWindow(
    instanceId: String,
    fromNetworkCallId: String?,
    onCloseRequest: () -> Unit,
) {
    val windowState: FloconWindowState = remember(instanceId) {
        createFloconWindowState()
    }
    val viewModel: NetworkMocksViewModel = koinViewModel()
    LaunchedEffect(viewModel, fromNetworkCallId) {
        viewModel.initWith(fromNetworkCallId)
    }
    val mocks by viewModel.items.collectAsStateWithLifecycle()
    val editionWindow by viewModel.editionWindow.collectAsStateWithLifecycle()
    key(instanceId, windowState) {
        FloconWindow(
            title = "Mocks",
            state = windowState,
            onCloseRequest = onCloseRequest,
        ) {
            NetworkMocksContent(
                mocks = mocks,
                modifier = Modifier.fillMaxSize(),
                onItemClicked = viewModel::clickOnMock,
                onAddItemClicked = viewModel::createNewMock,
                onDeleteClicked = viewModel::deleteMock,
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
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconSurface(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(FloconTheme.colorPalette.panel)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Mocks",
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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Create",
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.panel,
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(mocks) {
                    MockLineView(
                        item = it,
                        onClicked = onItemClicked,
                        onDeleteClicked = onDeleteClicked,
                        modifier = Modifier.fillMaxWidth(),
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
            onItemClicked = {},
            onDeleteClicked = {},
            onAddItemClicked = {},
        )
    }
}
