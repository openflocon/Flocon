package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.ui.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
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
    val filters by viewModel.filters.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkScreen(
        uiState = uiState,
        onAction = viewModel::onAction,

        filters = filters,
        modifier = modifier,
    )
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    onAction: (NetworkAction) -> Unit,

    filters: List<Filters>,
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
                    networkItems = uiState.items,
                    filters = filters,
                    onResetClicked = { onAction(NetworkAction.Reset) },
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
                                    enabled = uiState.detailState != null,
                                    onClick = { onAction(NetworkAction.ClosePanel) }
                                ),
                    ) {
                        items(filteredItems) {
                            NetworkItemView(
                                state = it,
                                columnWidths = columnWidths,
                                modifier = Modifier.fillMaxWidth(),
                                onAction = onAction
                            )
                        }
                    }
                }
            }
            AnimatedContent(
                targetState = uiState.detailState,
                transitionSpec = {
                    slideIntoContainer(SlideDirection.Start)
                        .togetherWith(slideOutOfContainer(SlideDirection.End))
                },
                contentKey = { it != null },
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
            ) {
                it?.let {
                    NetworkDetailView(
                        modifier = Modifier
                            .fillMaxHeight()
                            .requiredWidth(500.dp),
                        state = it,
                        onCopy = { onAction(NetworkAction.CopyText(it)) },
                    )
                } ?: Box(Modifier.matchParentSize())
            }
        }
    }
}

@Composable
@Preview
private fun NetworkScreenPreview() {
    FloconTheme {
        val uiState = NetworkUiState(
            items = remember {
                listOf(
                    previewNetworkItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                )
            }
        )

        NetworkScreen(
            uiState = uiState,
            onAction = {},
            filters = emptyList()
        )
    }
}
