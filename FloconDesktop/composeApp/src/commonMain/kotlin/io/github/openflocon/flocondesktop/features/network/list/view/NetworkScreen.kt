package io.github.openflocon.flocondesktop.features.network.list.view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.WifiTethering
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.composeunstyled.DropdownPanelAnchor
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import flocondesktop.composeapp.generated.resources.clear_old_sessions
import flocondesktop.composeapp.generated.resources.export_csv
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.invert_list
import flocondesktop.composeapp.generated.resources.network_bad_network
import flocondesktop.composeapp.generated.resources.network_deep_search
import flocondesktop.composeapp.generated.resources.network_display_old_sessions
import flocondesktop.composeapp.generated.resources.network_import_csv
import flocondesktop.composeapp.generated.resources.network_mocks_title
import flocondesktop.composeapp.generated.resources.network_pin_panel
import flocondesktop.composeapp.generated.resources.network_request
import flocondesktop.composeapp.generated.resources.network_response
import flocondesktop.composeapp.generated.resources.network_select_items
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.network.badquality.list.view.BadNetworkQualityWindow
import io.github.openflocon.flocondesktop.features.network.list.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemColumnWidths
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.model.previewGraphQlItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.previewNetworkUiState
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkPageHeaderView
import io.github.openflocon.flocondesktop.features.network.list.view.header.NetworkItemHeaderView
import io.github.openflocon.flocondesktop.features.network.websocket.NetworkWebsocketMockWindow
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconMenuItem
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
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
    val showEmptyState by viewModel.showEmptyState.collectAsStateWithLifecycle()

    NetworkScreen(
        uiState = uiState,
        rows = items,
        filterText = filterText,
        showEmptyState = showEmptyState,
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
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
    val columnWidths: NetworkItemColumnWidths = remember { NetworkItemColumnWidths() }

    LaunchedEffect(uiState.settings.autoScroll, rows.itemCount) {
        if (uiState.settings.autoScroll && rows.itemCount != -1) {
            lazyListState.animateScrollToItem(rows.itemCount)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
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
            NetworkPageHeaderView(
                requestCount = rows.itemCount,
                modifier = Modifier.padding(
                    top = FloconTheme.spacing.small,
                    start = FloconTheme.spacing.small,
                    end = FloconTheme.spacing.small
                )
            )
            FloconPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FloconTheme.spacing.small),
                filterBar = {
                    FilterBar(
                        filterText = filterText,
                        placeholderText = stringResource(Res.string.filter),
                        onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                        modifier = Modifier.fillMaxWidth(.7f),
                        containerColor = FloconTheme.colors.bg200,
                    )
                },
                actions = {
                    FloconIconButton(
                        imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                        onClick = { onAction(NetworkAction.OpenDeepSearch) },
                        tooltip = stringResource(Res.string.network_deep_search)
                    )
                    FloconIconToggleButton(
                        value = uiState.filterState.hasMocks,
                        tooltip = stringResource(Res.string.network_mocks_title),
                        onValueChange = { onAction(NetworkAction.OpenMocks) }
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.WifiTethering
                        )
                    }
                    FloconIconToggleButton(
                        value = uiState.filterState.hasBadNetwork,
                        tooltip = stringResource(Res.string.network_bad_network),
                        onValueChange = { onAction(NetworkAction.OpenBadNetworkQuality) }
                    ) {
                        FloconIcon(
                            imageVector = Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4
                        )
                    }
                    FloconIconToggleButton(
                        value = uiState.filterState.displayOldSessions,
                        tooltip = stringResource(Res.string.network_display_old_sessions),
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
//                            text = stringResource(Res.string.network_header_request_time),
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = stringResource(Res.string.network_header_method),
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = stringResource(Res.string.network_header_host),
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = stringResource(Res.string.network_header_request),
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = stringResource(Res.string.network_header_status),
//                            onCheckedChange = {}
//                        )
//                        FloconDropdownMenuItem(
//                            checked = true,
//                            text = stringResource(Res.string.network_header_time),
//                            onCheckedChange = {}
//                        )
//                    }
                    FloconOverflow {
                        FloconDropdownMenuItem(
                            text = stringResource(Res.string.export_csv),
                            leadingIcon = Icons.Outlined.Upload,
                            onClick = { onAction(NetworkAction.ExportCsv) }
                        )
                        FloconDropdownMenuItem(
                            text = stringResource(Res.string.network_import_csv),
                            leadingIcon = Icons.Outlined.Download,
                            onClick = { onAction(NetworkAction.ImportFromCsv) }
                        )
                        FloconDropdownMenuItem(
                            checked = uiState.settings.autoScroll,
                            text = stringResource(Res.string.auto_scroll),
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
                            text = stringResource(Res.string.invert_list),
                            leadingIcon = Icons.AutoMirrored.Outlined.List,
                            onCheckedChange = { checked -> onAction(NetworkAction.InvertList(checked)) }
                        )
                        FloconDropdownMenuItem(
                            checked = uiState.settings.pinPanel,
                            text = stringResource(Res.string.network_pin_panel),
                            leadingIcon = Icons.Sharp.PushPin,
                            onCheckedChange = { checked ->
                                onAction(NetworkAction.Pinned(checked))
                                it()
                            }
                        )
                        FloconDropdownSeparator()
                        var defaultTabMenuExpanded by remember { mutableStateOf(false) }

                        FloconDropdownMenu(
                            expanded = defaultTabMenuExpanded,
                            onDismissRequest = { defaultTabMenuExpanded = false },
                            onExpandRequest = { defaultTabMenuExpanded = true },
                            anchor = DropdownPanelAnchor.BottomStart,
                            anchorContent = {
                                FloconMenuItem(
                                    text = "Default detail tab: ${uiState.settings.defaultSelectedTab.name}",
                                    leadingIcon = Icons.Outlined.Layers,
                                    trailingIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                    onClick = { defaultTabMenuExpanded = !defaultTabMenuExpanded }
                                )
                            }
                        ) {
                            FloconDropdownMenuItem(
                                text = stringResource(Res.string.network_request),
                                leadingIcon = Icons.Outlined.Upload,
                                trailingIcon = if (uiState.settings.defaultSelectedTab == NetworkDetailTab.Request) Icons.Outlined.Check else null,
                                onClick = {
                                    onAction(NetworkAction.SetDefaultSelectedTab(NetworkDetailTab.Request))
                                    defaultTabMenuExpanded = false
                                }
                            )
                            FloconDropdownMenuItem(
                                text = stringResource(Res.string.network_response),
                                leadingIcon = Icons.Outlined.Download,
                                trailingIcon = if (uiState.settings.defaultSelectedTab == NetworkDetailTab.Response) Icons.Outlined.Check else null,
                                onClick = {
                                    onAction(NetworkAction.SetDefaultSelectedTab(NetworkDetailTab.Response))
                                    defaultTabMenuExpanded = false
                                }
                            )
                        }


                        FloconDropdownMenuItem(
                            text = stringResource(Res.string.network_select_items),
                            leadingIcon = Icons.Sharp.SelectAll,
                            onClick = { onAction(NetworkAction.MultiSelect) }
                        )
                        FloconDropdownSeparator()
                        FloconDropdownMenuItem(
                            text = stringResource(Res.string.clear_old_sessions),
                            leadingIcon = Icons.Outlined.CleaningServices,
                            onClick = { onAction(NetworkAction.ClearOldSession) }
                        )
                    }
                }
            )
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                NetworkItemHeaderView(
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
                Row(
                    Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = lazyListState,
                        reverseLayout = uiState.settings.invertList,
                        contentPadding = PaddingValues(
                            horizontal = FloconTheme.spacing.small,
                            vertical = FloconTheme.spacing.extraSmall,
                        ),
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
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
                    if (showEmptyState) {
                        FeatureEmptyState(
                            doc = FeatureDoc.Network,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    this@Row.AnimatedVisibility(
                        visible = uiState.contentState.selecting,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it },
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = FloconTheme.spacing.small,
                                    horizontal = FloconTheme.spacing.medium
                                )
                                .clip(FloconTheme.shapes.medium)
                                .background(FloconTheme.colors.bg200)
                                .padding(FloconTheme.spacing.small)
                        ) {
                            Text(
                                text = "${uiState.contentState.multiSelectedIds.size} items selected",
                                style = FloconTheme.typography.bodySmall,
                                color = FloconTheme.colors.inkMuted,
                                modifier = Modifier.padding(start = FloconTheme.spacing.small),
                            )
                            Spacer(Modifier.weight(1f))
                            FloconIconTonalButton(
                                onClick = { onAction(NetworkAction.ExportCsv) },
                                containerColor = FloconTheme.colors.ink
                            ) {
                                FloconIcon(
                                    Icons.Sharp.Upload
                                )
                            }
                            FloconIconTonalButton(
                                onClick = { onAction(NetworkAction.DeleteSelection) },
                                containerColor = FloconTheme.colors.ink
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
                                    tint = FloconTheme.colors.inkMuted
                                )
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
): NetworkItemViewState? =
    rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
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
): NetworkItemViewState? =
    rows.itemSnapshotList.indexOfFirst { it?.uuid == uiState.contentState.selectedRequestId }
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
            filterText = mutableStateOf(""),
            showEmptyState = false,
        )
    }
}
