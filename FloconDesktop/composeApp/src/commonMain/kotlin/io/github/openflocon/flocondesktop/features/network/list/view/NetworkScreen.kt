package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.openflocon.flocondesktop.features.network.list.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkPageHeader
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkRowsList
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkTopBar
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkWindows
import io.github.openflocon.flocondesktop.features.network.list.view.components.networkKeyboardNavigation
import io.github.openflocon.flocondesktop.features.network.list.view.header.NetworkItemHeader
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.floconIsland
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: NetworkViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items: LazyPagingItems<NetworkItemViewState> = viewModel.items.collectAsLazyPagingItems()
    val filterText = viewModel.filterText

    NetworkScreen(
        uiState = uiState,
        rows = items,
        filterText = filterText,
        showEmptyState = uiState.showEmptyState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    rows: LazyPagingItems<NetworkItemViewState>,
    filterText: State<String>,
    showEmptyState: Boolean,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val columnWidths: NetworkItemColumnWidths = remember { NetworkItemColumnWidths() }

    LaunchedEffect(uiState.settings.autoScroll, rows.itemCount) {
        if (uiState.settings.autoScroll && rows.itemCount != -1) {
            lazyListState.animateScrollToItem(rows.itemCount)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clip(FloconTheme.shapes.large)
                .floconIsland(FloconTheme.colors.bg100)
                .networkKeyboardNavigation(
                    rows = rows,
                    uiState = uiState,
                    onAction = onAction,
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = uiState.contentState.selectedRequestId != null,
                    onClick = { onAction(NetworkAction.ClosePanel) },
                )
                .mascotPlatform()
        ) {
            NetworkPageHeader(
                requestCount = rows.itemCount,
                modifier = Modifier.padding(
                    top = FloconTheme.spacing.small,
                    start = FloconTheme.spacing.small,
                    end = FloconTheme.spacing.small
                )
            )
            NetworkTopBar(
                filterText = filterText,
                filterState = uiState.filterState,
                settings = uiState.settings,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FloconTheme.spacing.small),
            )
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NetworkItemHeader(
                    columnWidths = columnWidths,
                    modifier = Modifier
                        .fillMaxWidth(),
                    clickOnSort = { type, sort ->
                        onAction(NetworkAction.HeaderAction.ClickOnSort(type, sort))
                    },
                    onFilterAction = {
                        onAction(NetworkAction.HeaderAction.FilterAction(it))
                    },
                    state = uiState.headerState,
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(FloconTheme.colors.line)
                )
                NetworkRowsList(
                    rows = rows,
                    contentState = uiState.contentState,
                    invertList = uiState.settings.invertList,
                    showEmptyState = showEmptyState,
                    columnWidths = columnWidths,
                    lazyListState = lazyListState,
                    onAction = onAction,
                )
            }
        }
    }

    NetworkWindows(
        contentState = uiState.contentState,
        onAction = onAction,
    )
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
private fun NetworkScreenPreview() {
    FloconTheme {
        val uiState = previewNetworkUiState()
        val rows = remember {
            MutableStateFlow(
                PagingData.from(
                    listOf(
                        previewNetworkItemViewState(),
                        previewNetworkItemViewState(),
                        previewGraphQlItemViewState(),
                        previewNetworkItemViewState(),
                        previewGraphQlItemViewState(),
                        previewNetworkItemViewState(),
                    )
                )
            )
        }.collectAsLazyPagingItems()

        NetworkScreen(
            uiState = uiState,
            rows = rows,
            onAction = {},
            filterText = remember { mutableStateOf("") },
            showEmptyState = false,
        )
    }
}
