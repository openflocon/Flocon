package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.outlined.SwapHorizontalCircle
import androidx.compose.material.icons.outlined.SwapVerticalCircle
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconOverflowItem
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkScreen(modifier: Modifier = Modifier) {
    val viewModel: NetworkViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun NetworkHeaderButton(
    title: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(FloconTheme.colorPalette.surface)
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface),
        )
        Text(
            title,
            style = FloconTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Normal,
                color = FloconTheme.colorPalette.onSurface,
            ),
        )
    }
}

@Composable
fun NetworkScreen(
    uiState: NetworkUiState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
    val columnWidths: NetworkItemColumnWidths =
        remember { NetworkItemColumnWidths() } // Default widths provided

    FloconSurface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        enabled = uiState.detailState != null,
                        onClick = { onAction(NetworkAction.ClosePanel) },
                    ),
            ) {
                FloconPageTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    filterBar = {
                        FilterBar(
                            placeholderText = "Filter route",
                            onTextChange = { onAction(NetworkAction.FilterQuery(it)) },
                            modifier = Modifier.weight(1f),
                        )
                    },
                    actions = {
                        FloconIconButton(
                            imageVector = Icons.Outlined.Delete,
                            onClick = { onAction(NetworkAction.Reset) },
                        )
                        NetworkHeaderButton(
                            title = "Mocks",
                            imageVector = Icons.Outlined.WifiTethering,
                            onClick = {
                                onAction(NetworkAction.OpenMocks)
                            },
                        )
                        NetworkHeaderButton(
                            title = "Bad Network Quality",
                            imageVector = Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4,
                            onClick = {
                                onAction(NetworkAction.OpenBadNetworkQuality)
                            },
                        )
                        FloconOverflow(
                            items = listOf(
                                FloconOverflowItem(
                                    text = "Export Csv",
                                    onClick = { onAction(NetworkAction.ExportCsv) },
                                ),
                            ),
                        )
                    },
                )
                NetworkItemHeaderView(
                    columnWidths = columnWidths,
                    modifier = Modifier.fillMaxWidth()
                        .background(FloconTheme.colorPalette.background),
                    clickOnSort = { type, sort ->
                        onAction(NetworkAction.HeaderAction.ClickOnSort(type, sort))
                    },
                    onFilterAction = {
                        onAction(NetworkAction.HeaderAction.FilterAction(it))
                    },
                    state = uiState.headerState,
                )
                Row(
                    Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight(),
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
            FloconPanel(
                contentState = uiState.detailState,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                NetworkDetailView(
                    modifier = Modifier.fillMaxSize(),
                    state = it,
                    onAction = onAction,
                )
            }
        }
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
