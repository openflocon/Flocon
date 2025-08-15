package io.github.openflocon.flocondesktop.features.network.view.mocks

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.model.mocks.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.model.mocks.previewMockNetworkLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
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
        BasicAlertDialog(
            onDismissRequest = onCloseRequest,
        ) {
            NetworkMocksContent(
                mocks = mocks,
                modifier = Modifier.fillMaxSize()
                    .padding(vertical = 100.dp)
                    .clip(RoundedCornerShape(8.dp)),
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
    FloconSurface(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(FloconTheme.colorPalette.panel)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
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
                modifier = Modifier.fillMaxSize(),
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
