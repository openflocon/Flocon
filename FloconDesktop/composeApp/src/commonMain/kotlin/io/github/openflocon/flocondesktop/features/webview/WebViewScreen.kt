package io.github.openflocon.flocondesktop.features.webview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.general_delete
import flocondesktop.composeapp.generated.resources.webview_empty
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.webview.model.WebViewAction
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventKindUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewEventUiModel
import io.github.openflocon.flocondesktop.features.webview.model.WebViewFilterUiModel
import io.github.openflocon.flocondesktop.features.webview.model.previewWebViewEventUiModel
import io.github.openflocon.flocondesktop.features.webview.view.WebViewEventRow
import io.github.openflocon.flocondesktop.features.webview.view.WebViewFilters
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WebViewScreen(modifier: Modifier = Modifier) {
    val viewModel: WebViewViewModel = koinViewModel()
    val events = viewModel.events.collectAsLazyPagingItems()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    WebViewScreen(
        events = events,
        selectedFilter = selectedFilter,
        onFilterSelected = viewModel::onFilterSelected,
        filterText = viewModel.filterText,
        onFilterTextChanged = viewModel::onFilterTextChanged,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
internal fun WebViewScreen(
    events: LazyPagingItems<WebViewEventUiModel>,
    selectedFilter: WebViewFilterUiModel,
    onFilterSelected: (WebViewFilterUiModel) -> Unit,
    filterText: State<String>,
    onFilterTextChanged: (String) -> Unit,
    onAction: (WebViewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier,
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = stringResource(Res.string.filter),
                    onTextChange = onFilterTextChanged,
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(FloconTheme.shapes.small)
                        .clickable { onAction(WebViewAction.Clear) },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(Res.string.general_delete),
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(FloconTheme.colors.ink),
                    )
                }
            },
        )

        WebViewFilters(
            selected = selectedFilter,
            onFilterSelected = onFilterSelected,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colors.bg100)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colors.bg200,
                    shape = FloconTheme.shapes.medium,
                )
                .mascotPlatform(),
        ) {
            if (events.itemCount == 0) {
                Text(
                    text = stringResource(Res.string.webview_empty),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = FloconTheme.spacing.small),
                ) {
                    items(
                        count = events.itemCount,
                        key = events.itemKey { it.id },
                    ) { index ->
                        events[index]?.let { event ->
                            WebViewEventRow(
                                event = event,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.TopEnd),
                )
            }
        }
    }
}

@Composable
@Preview
private fun WebViewScreenPreview() {
    FloconTheme {
        val events = flowOf(
            PagingData.from(
                listOf(
                    previewWebViewEventUiModel().copy(id = "1"),
                    previewWebViewEventUiModel(isError = true).copy(id = "2"),
                    previewWebViewEventUiModel(kind = WebViewEventKindUiModel.Page).copy(id = "3"),
                )
            )
        ).collectAsLazyPagingItems()

        WebViewScreen(
            events = events,
            selectedFilter = WebViewFilterUiModel.All,
            onFilterSelected = {},
            filterText = remember { mutableStateOf("") },
            onFilterTextChanged = {},
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
