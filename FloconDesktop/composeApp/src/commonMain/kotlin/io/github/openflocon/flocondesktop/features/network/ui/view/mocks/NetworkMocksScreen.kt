package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkMocksViewModel
import io.github.openflocon.flocondesktop.features.network.ui.model.mocks.MockNetworkLineUiModel
import io.github.openflocon.flocondesktop.features.network.ui.view.mocks.NetworkEditionWindow
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkMocksWindow(
    onCloseRequest: () -> Unit,
) {
    val windowState: FloconWindowState = remember {
        createFloconWindowState()
    }
    val viewModel : NetworkMocksViewModel = koinViewModel()
    val mocks by viewModel.items.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
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
        )
    }

    selectedItem?.let {
        NetworkEditionWindow(
            state =it,
            onCloseRequest = viewModel::cancelMockCreation,
            onCancel = viewModel::cancelMockCreation,
            onSave = viewModel::addMock,
        )
    }
}


@Composable
private fun NetworkMocksContent(
    mocks: List<MockNetworkLineUiModel>,
    onItemClicked: (id: String) -> Unit,
    onAddItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconSurface(
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(mocks) {
                    MockLineView(
                        item = it,
                        onClicked = onItemClicked,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            FloatingActionButton(
                onClick = {
                    onAddItemClicked()
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun MockLineView(
    item: MockNetworkLineUiModel,
    onClicked: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable {
            onClicked(item.id)
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(item.method)
        Spacer(modifier = Modifier.width(100.dp))
        Text(item.urlPattern)
    }
}
