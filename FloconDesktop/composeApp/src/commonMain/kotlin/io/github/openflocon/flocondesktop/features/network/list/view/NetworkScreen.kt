package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.badquality.list.view.BadNetworkQualityWindow
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailView
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
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMocksWindow
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi
import io.github.openflocon.flocondesktop.features.network.view.NetworkBodyWindow
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconIconToggleButton
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: NetworkViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {

    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
    val columnWidths: NetworkItemColumnWidths = remember { NetworkItemColumnWidths() } // Default widths provided

    LaunchedEffect(uiState.contentState.autoScroll, uiState.items) {
        if (uiState.contentState.autoScroll && uiState.items.lastIndex != -1) {
            lazyListState.animateScrollToItem(uiState.items.lastIndex)
        }
    }

    FloconFeature(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = null,
                indication = null,
                enabled = uiState.detailState != null,
                onClick = { onAction(NetworkAction.ClosePanel) },
            )
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            filterBar = {
                FilterBar(
                    placeholderText = "Filter route",
                    onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                    modifier = Modifier.fillMaxWidth(.7f),
                )
            },
            actions = {
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
                        leadingIcon = Icons.Outlined.ImportExport,
                        onClick = { onAction(NetworkAction.ExportCsv) }
                    )
                    FloconDropdownMenuItem(
                        checked = uiState.contentState.autoScroll,
                        text = "Auto scroll",
                        leadingIcon = Icons.Outlined.PlayCircle,
                        onCheckedChange = { onAction(NetworkAction.ToggleAutoScroll) }
                    )
                    FloconDropdownMenuItem(
                        checked = uiState.contentState.invertList,
                        text = "Invert list",
                        leadingIcon = Icons.AutoMirrored.Outlined.List,
                        onCheckedChange = { onAction(NetworkAction.InvertList(it)) }
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
                LazyColumn(
                    state = lazyListState,
                    reverseLayout = uiState.contentState.invertList,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    items(
                        items = uiState.items,
                        key = NetworkItemViewState::uuid,
                    ) {
                        NetworkItemView(
                            state = it,
                            columnWidths = columnWidths,
                            onAction = onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        )
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
    FloconPanel(
        contentState = uiState.detailState,
        onClose = { onAction(NetworkAction.ClosePanel) }
    ) {
        NetworkDetailView(
            state = it,
            onAction = onAction,
            modifier = Modifier.matchParentSize()
        )
    }

    val states = remember { mutableStateMapOf<NetworkBodyDetailUi, FloconWindowState>() }

    LaunchedEffect(uiState.contentState.detailJsons) {
        val deletedJson =
            states.keys.filter { key -> uiState.contentState.detailJsons.none { key.id == it.id } }
        val addedJson =
            uiState.contentState.detailJsons.filter { key -> states.keys.none { key.id == it.id } }

        deletedJson.forEach { states.remove(it) }
        addedJson.forEach {
            states.put(
                it, createFloconWindowState(),
            )
        }
    }

    uiState.contentState
        .detailJsons
        .forEach { item ->
            val state = states[item]

            if (state != null) {
                NetworkBodyWindow(
                    body = item,
                    state = state,
                    onCloseRequest = {
                        onAction(NetworkAction.CloseJsonDetail(item.id))
                    },
                )
            }
        }

    uiState.contentState.mocksDisplayed?.let {
        NetworkMocksWindow(
            instanceId = it.windowInstanceId,
            fromNetworkCallId = it.fromNetworkCallId,
            onCloseRequest = {
                onAction(NetworkAction.CloseMocks)
            },
        )
    }

    if (uiState.contentState.badNetworkQualityDisplayed) {
        BadNetworkQualityWindow(
            onCloseRequest = {
                onAction(NetworkAction.CloseBadNetworkQuality)
            },
        )
    }
}

@Composable
@Preview
private fun NetworkScreenPreview() {
    FloconTheme {
        val uiState = previewNetworkUiState().copy(
            items = remember {
                listOf(
                    previewNetworkItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                    previewGraphQlItemViewState(),
                    previewNetworkItemViewState(),
                )
            },
        )

        NetworkScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}
