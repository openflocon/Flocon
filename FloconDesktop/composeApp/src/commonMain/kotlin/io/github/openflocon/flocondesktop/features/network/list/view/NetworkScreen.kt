package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.outlined.Upgrade
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material.icons.sharp.ArrowUpward
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.PushPin
import androidx.compose.material.icons.sharp.SelectAll
import androidx.compose.material.icons.sharp.Upload
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import io.github.openflocon.flocondesktop.features.network.badquality.list.view.BadNetworkQualityWindow
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailContent
import io.github.openflocon.flocondesktop.features.network.list.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.network.list.view.header.NetworkItemHeaderView
import io.github.openflocon.flocondesktop.features.network.websocket.NetworkWebsocketMockWindow
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconAnimateVisibility
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.panel.PANEL_WIDTH
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
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
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    rows: LazyPagingItems<NetworkItemViewState>,
    filterText: State<String>,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
    val columnWidths: NetworkItemColumnWidths =
        remember { NetworkItemColumnWidths() } // Default widths provided

    LaunchedEffect(uiState.settings.autoScroll, rows.itemCount) {
        if (uiState.settings.autoScroll && rows.itemCount != -1) {
            lazyListState.animateScrollToItem(rows.itemCount)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloconFeature(
            modifier = modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = uiState.detailState != null,
                    onClick = { onAction(NetworkAction.ClosePanel) },
                )
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown)
                        return@onPreviewKeyEvent false

                    when (event.key) {
                        Key.DirectionUp -> {
                            selectPreviousRow(rows, uiState)
                                ?.let {
                                    onAction(NetworkAction.Up(it.uuid))
                                }

                            true
                        }

                        Key.DirectionDown -> {
                            selectNextRow(rows, uiState)
                                ?.let {
                                    onAction(NetworkAction.Down(it.uuid))
                                }
                            true
                        }

                        Key.F -> {
                            if (event.isMetaPressed) {
                                onAction(NetworkAction.OpenDeepSearch)
                                true
                            } else {
                                false
                            }
                        }

                        else -> false
                    }
                }
        ) {
            FloconPageTopBar(
                modifier = Modifier.fillMaxWidth(),
                filterBar = {
                    FilterBar(
                        filterText = filterText,
                        placeholderText = stringResource(Res.string.filter),
                        onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                        modifier = Modifier.fillMaxWidth(.7f),
                    )
                },
                actions = {
                    FloconIconButton(
                        imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                        onClick = { onAction(NetworkAction.OpenDeepSearch) },
                        tooltip = "Deep Search"
                    )
                    FloconIconToggleButton(
                        value = uiState.filterState.hasMocks,
                        tooltip = "Mocks",
                        onValueChange = { onAction(NetworkAction.OpenMocks) }
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.WifiTethering
                        )
                    }
                    FloconIconToggleButton(
                        value = uiState.filterState.hasBadNetwork,
                        tooltip = "Bad network",
                        onValueChange = { onAction(NetworkAction.OpenBadNetworkQuality) }
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4
                        )
                    }
                    FloconIconToggleButton(
                        value = uiState.filterState.displayOldSessions,
                        tooltip = "Display old sessions",
                        onValueChange = { onAction(NetworkAction.UpdateDisplayOldSessions(it)) }
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.History
                        )
                    }
                    FloconIconButton(
                        imageVector = Icons.Outlined.Delete,
                        onClick = { onAction(NetworkAction.Reset) }
                    )
                    // TODO Later
//                    FloconDropdownMenu(
//                        expanded = expandedColumn,
//                        anchorContent = {
//                            FloconIconButton(
//                                imageVector = Icons.Outlined.ViewColumn,
//                                onClick = { expandedColumn = true }
//                            )
//                        },
//                        onExpandRequest = { expandedColumn = true },
//                        onDismissRequest = { expandedColumn = false }
//                    ) {
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Request Time",
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Method",
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Domain",
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Query",
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Status",
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = "Time",
//                            onCheckedChange = {}
//                        )
//                    }
                    FloconOverflow {
                        FloconDropdownMenuItem(
                            text = "Export CSV",
                            leadingIcon = Icons.Outlined.Upload,
                            onClick = { onAction(NetworkAction.ExportCsv) }
                        )
                        FloconDropdownMenuItem(
                            text = "Import From CSV",
                            leadingIcon = Icons.Outlined.Download,
                            onClick = { onAction(NetworkAction.ImportFromCsv) }
                        )
                        FloconDropdownMenuItem(
                            checked = uiState.settings.autoScroll,
                            text = "Auto scroll",
                            leadingIcon = Icons.Outlined.PlayCircle,
                            onCheckedChange = { checked ->
                                onAction(
                                    NetworkAction.ToggleAutoScroll(
                                        checked
                                    )
                                )
                            }
                        )
                        FloconDropdownMenuItem(
                            checked = uiState.settings.invertList,
                            text = "Invert list",
                            leadingIcon = Icons.AutoMirrored.Outlined.List,
                            onCheckedChange = { checked -> onAction(NetworkAction.InvertList(checked)) }
                        )
                        FloconDropdownMenuItem(
                            checked = uiState.settings.pinPanel,
                            text = "Pin panel",
                            leadingIcon = Icons.Sharp.PushPin,
                            onCheckedChange = { checked ->
                                onAction(NetworkAction.Pinned(checked))
                                it()
                            }
                        )
                        FloconDropdownMenuItem(
                            text = "Select items",
                            leadingIcon = Icons.Sharp.SelectAll,
                            onClick = { onAction(NetworkAction.MultiSelect) }
                        )
                        FloconDropdownSeparator()
                        FloconDropdownMenuItem(
                            text = "Clear old sessions",
                            leadingIcon = Icons.Outlined.CleaningServices,
                            onClick = { onAction(NetworkAction.ClearOldSession) }
                        )
                    }
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
            ) {
                NetworkItemHeaderView(
                    columnWidths = columnWidths,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FloconTheme.colorPalette.primary),
                    clickOnSort = { type, sort ->
                        onAction(NetworkAction.HeaderAction.ClickOnSort(type, sort))
                    },
                    onFilterAction = {
                        onAction(NetworkAction.HeaderAction.FilterAction(it))
                    },
                    state = uiState.headerState,
                )
                FloconHorizontalDivider()
                Row(
                    Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyColumn(
                            state = lazyListState,
                            reverseLayout = uiState.settings.invertList,
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            items(
                                count = rows.itemCount,
                                key = rows.itemKey { it.uuid },
                            ) { index ->
                                val item = rows[index]
                                if (item != null) {
                                    NetworkItemView(
                                        state = item,
                                        selected = item.uuid == uiState.contentState.selectedRequestId,
                                        columnWidths = columnWidths,
                                        onAction = onAction,
                                        multiSelect = uiState.contentState.selecting,
                                        multiSelected = uiState.contentState.multiSelectedIds.contains(
                                            item.uuid
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                    )
                                } else {
                                    Box(Modifier) // display nothing during load
                                }
                            }
                        }
                        this@Row.AnimatedVisibility(
                            visible = uiState.contentState.selecting,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .clip(FloconTheme.shapes.medium)
                                    .background(FloconTheme.colorPalette.secondary)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${uiState.contentState.multiSelectedIds.size} items selected",
                                    style = FloconTheme.typography.bodySmall,
                                    color = FloconTheme.colorPalette.onSecondary,
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                FloconIconTonalButton(
                                    onClick = { onAction(NetworkAction.ExportCsv) },
                                    containerColor = FloconTheme.colorPalette.tertiary
                                ) {
                                    FloconIcon(
                                        Icons.Sharp.Upload
                                    )
                                }
                                FloconIconTonalButton(
                                    onClick = { onAction(NetworkAction.DeleteSelection) },
                                    containerColor = FloconTheme.colorPalette.tertiary
                                ) {
                                    FloconIcon(
                                        Icons.Sharp.Delete
                                    )
                                }
                                FloconButton(
                                    onClick = { onAction(NetworkAction.ClearMultiSelect) },
                                    containerColor = Color.Transparent,
                                ) {
                                    FloconIcon(
                                        Icons.Sharp.Close,
                                        tint = FloconTheme.colorPalette.onSecondary
                                    )
                                }
                            }
                        }
                    }
                    FloconVerticalScrollbar(
                        adapter = scrollAdapter,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
        }
        FloconAnimateVisibility(
            state = uiState.detailState,
            modifier = Modifier.fillMaxHeight()
        ) {
            Row {
                Spacer(Modifier.width(8.dp))
                NetworkDetailContent(
                    uiState = it,
                    onAction = { action -> onAction(NetworkAction.DetailAction(action)) },
                    modifier = Modifier
                        .width(PANEL_WIDTH)
                        .clip(FloconTheme.shapes.medium)
                )
            }
        }
    }

    if (uiState.contentState.badNetworkQualityDisplayed) {
        BadNetworkQualityWindow(
            onCloseRequest = {
                onAction(NetworkAction.CloseBadNetworkQuality)
            },
        )
    }

    if (uiState.contentState.websocketMocksDisplayed) {
        NetworkWebsocketMockWindow(
            onCloseRequest = {
                onAction(NetworkAction.CloseWebsocketMocks)
            }
        )
    }
}

private fun selectPreviousRow(
    rows: LazyPagingItems<NetworkItemViewState>,
    uiState: NetworkUiState
): NetworkItemViewState? = rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
    .takeIf { it != -1 }
    ?.let { selectedIndex ->
        val newIndex = if (uiState.settings.invertList)
            selectedIndex + 1
        else
            selectedIndex - 1
        newIndex.takeIf { it > 0 && it <= rows.itemSnapshotList.lastIndex }
    }?.let {
        rows[it]
    }

private fun selectNextRow(
    rows: LazyPagingItems<NetworkItemViewState>,
    uiState: NetworkUiState
): NetworkItemViewState? = rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
    .takeIf { it != -1 }
    ?.let { selectedIndex ->
        val newIndex = if (uiState.settings.invertList)
            selectedIndex - 1
        else
            selectedIndex + 1
        newIndex.takeIf { it > 0 && it <= rows.itemSnapshotList.lastIndex }
    }?.let {
        rows[it]
    }

@Composable
@Preview
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
            filterText = mutableStateOf(""),
        )
    }
}
