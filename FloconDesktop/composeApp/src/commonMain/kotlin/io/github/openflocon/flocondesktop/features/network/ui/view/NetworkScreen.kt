package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.network.ui.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.OnNetworkItemUserAction
import io.github.openflocon.flocondesktop.features.network.ui.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.components.NetworkFilter
import io.github.openflocon.flocondesktop.features.network.ui.view.components.NetworkItemHeaderView
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.Filters
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkScreen(modifier: Modifier = Modifier) {
    val viewModel: NetworkViewModel = koinViewModel()
    val items by viewModel.state.collectAsStateWithLifecycle()
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    NetworkScreen(
        networkItems = items,
        filters = viewModel.filters,
        modifier = modifier,
        detailState = detailState,
        onNetworkItemUserAction = viewModel::onNetworkItemUserAction,
        onCopyText = viewModel::onCopyText,
        onReset = viewModel::onReset,
        closeDetailPanel = viewModel::closeDetailPanel,
    )
}

@Composable
fun NetworkScreen(
    networkItems: List<NetworkItemViewState>,
    filters: List<Filters>,
    detailState: NetworkDetailViewState?,
    onNetworkItemUserAction: (OnNetworkItemUserAction) -> Unit,
    onCopyText: (String) -> Unit,
    closeDetailPanel: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnWidths: NetworkItemColumnWidths =
        remember { NetworkItemColumnWidths() } // Default widths provided

    var filteredItems by remember { mutableStateOf<List<NetworkItemViewState>>(emptyList()) }

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Network",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(all = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                NetworkFilter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconColors.pannel)
                        .padding(horizontal = 12.dp),
                    networkItems = networkItems,
                    filters = filters,
                    onResetClicked = onReset,
                    onItemsChange = {
                        filteredItems = it
                    },
                )
                NetworkItemHeaderView(
                    columnWidths = columnWidths,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    Modifier
                        .fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    enabled = detailState != null,
                                ) {
                                    closeDetailPanel()
                                },
                    ) {
                        items(filteredItems) {
                            NetworkItemView(
                                state = it,
                                columnWidths = columnWidths,
                                modifier = Modifier.fillMaxWidth(),
                                onUserAction = onNetworkItemUserAction,
                            )
                        }
                    }
                }
            }
            detailState?.let {
                NetworkDetailView(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight()
                            .width(500.dp),
                    state = it,
                    onCopy = onCopyText,
                )
            }
        }
    }
}

@Composable
@Preview
private fun NetworkScreenPreview() {
    FloconTheme {
        val networkItems =
            remember {
                listOf(
                    previewNetworkItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                )
            }
        NetworkScreen(
            networkItems = networkItems,
            filters = emptyList(),
            detailState = null,
            closeDetailPanel = {},
            onNetworkItemUserAction = {},
            onCopyText = {},
            onReset = {},
        )
    }
}
