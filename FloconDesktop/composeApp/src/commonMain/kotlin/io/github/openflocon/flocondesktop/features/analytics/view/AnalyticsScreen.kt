package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.export_csv
import flocondesktop.composeapp.generated.resources.invert_list
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsViewModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.max

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val itemsState by viewModel.itemsState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val rows = viewModel.rows.collectAsLazyPagingItems()
    val filterText = viewModel.filterText
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    AnalyticsScreen(
        screenState = screenState,
        itemsState = itemsState,
        onAnalyticsSelected = viewModel::onAnalyticsSelected,
        rows = rows,
        selectedItem = selectedItem,
        onResetClicked = viewModel::onResetClicked,
        modifier = modifier,
        onAction = viewModel::onAction,
        filterText = filterText,
        onFilterTextChanged = viewModel::onFilterTextChanged,
    )
}

@Composable
fun AnalyticsScreen(
    filterText: State<String>,
    screenState: AnalyticsScreenUiState,
    itemsState: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    onFilterTextChanged: (String) -> Unit,
    onResetClicked: () -> Unit,
    rows: LazyPagingItems<AnalyticsRowUiModel>,
    selectedItem: AnalyticsDetailUiModel?,
    onAction: (AnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    LaunchedEffect(screenState.autoScroll, rows.itemCount) {
        if (screenState.autoScroll && rows.itemSnapshotList.lastIndex != -1) {
            listState.animateScrollToItem(rows.itemSnapshotList.lastIndex)
        }
    }

    FloconFeature(
        modifier = modifier
            .clickable(
                interactionSource = null,
                indication = null,
                enabled = selectedItem != null,
                onClick = { onAction(AnalyticsAction.ClosePanel) }
            )
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                AnalyticsSelectorView(
                    analyticsState = itemsState,
                    onAnalyticsSelected = onAnalyticsSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            filterBar = {
                AnalyticsFilterBar(
                    onFilterTextChanged = onFilterTextChanged,
                    filterText = filterText,
                    onResetClicked = onResetClicked,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            actions = {
                FloconOverflow {
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.export_csv),
                        leadingIcon = Icons.Outlined.ImportExport,
                        onClick = { onAction(AnalyticsAction.ExportCsv) }
                    )
                    FloconDropdownMenuItem(
                        checked = screenState.autoScroll,
                        text = stringResource(Res.string.auto_scroll),
                        leadingIcon = Icons.Outlined.PlayCircle,
                        onCheckedChange = { onAction(AnalyticsAction.ToggleAutoScroll) }
                    )
                    FloconDropdownMenuItem(
                        checked = screenState.invertList,
                        text = stringResource(Res.string.invert_list),
                        leadingIcon = Icons.AutoMirrored.Outlined.List,
                        onCheckedChange = { onAction(AnalyticsAction.InvertList(it)) }
                    )
                    FloconDropdownSeparator()
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.clear_old_sessions),
                        leadingIcon = Icons.Outlined.CleaningServices,
                        onClick = { onAction(AnalyticsAction.ClearOldSession) }
                    )
                }
            }
        )

        var widthPx by remember { mutableStateOf(0f) }
        val density = LocalDensity.current.density
        val dpWidth = (widthPx / density).dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colorPalette.secondary,
                    shape = FloconTheme.shapes.medium
                )
                .onSizeChanged {
                    widthPx = max(widthPx, it.width.toFloat())
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
            ) {
                LazyColumn(
                    state = listState,
                    reverseLayout = screenState.invertList,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            widthPx = max(widthPx, it.width.toFloat())
                        },
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(
                        count = rows.itemCount,
                        key = rows.itemKey { it.id },
                    ) { index ->
                        val item = rows[index]
                        if (item != null) {
                            AnalyticsRowView(
                                model = item,
                                modifier = Modifier.fillMaxWidth(),
                                onAction = onAction,
                            )
                            if (index != rows.itemSnapshotList.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.width(dpWidth)
                                )
                            }
                        } else {
                            Box(Modifier) // display nothing during load
                        }
                    }
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight()
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
@Preview
private fun AnalyticsScreenPreview() {
    val rows = remember {
        flowOf(
            PagingData.from(
                listOf(
                    previewAnalyticsRowUiModel(),
                    previewAnalyticsRowUiModel(),
                    previewAnalyticsRowUiModel(),
                )
            )
        )
    }.collectAsLazyPagingItems()
    FloconTheme {
        AnalyticsScreen(
            itemsState = previewAnalyticsStateUiModel(),
            onAnalyticsSelected = {},
            selectedItem = null,
            rows = rows,
            onAction = {},
            modifier = Modifier.fillMaxSize(),
            screenState = previewAnalyticsScreenUiState(),
            onFilterTextChanged = {},
            onResetClicked = {},
            filterText = mutableStateOf(""),
        )
    }
}
