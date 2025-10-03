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
import androidx.compose.foundation.lazy.itemsIndexed
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
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsViewModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsAction
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsRowUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.DeviceAnalyticsUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.items
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsContentStateUiModel
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsScreenUiState
import io.github.openflocon.flocondesktop.features.analytics.model.previewAnalyticsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconDropdownSeparator
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconOverflow
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.panel.FloconPanel
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.max

@Composable
fun AnalyticsScreen(modifier: Modifier = Modifier) {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val itemsState by viewModel.itemsState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val rows by viewModel.content.collectAsStateWithLifecycle()
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
        content = rows,
        selectedItem = selectedItem,
        onResetClicked = viewModel::onResetClicked,
        modifier = modifier,
        onAction = viewModel::onAction,
    )
}

@Composable
fun AnalyticsScreen(
    screenState: AnalyticsScreenUiState,
    itemsState: AnalyticsStateUiModel,
    onAnalyticsSelected: (DeviceAnalyticsUiModel) -> Unit,
    content: AnalyticsContentStateUiModel,
    onResetClicked: () -> Unit,
    selectedItem: AnalyticsDetailUiModel?,
    onAction: (AnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var analyticsItems by remember { mutableStateOf<List<AnalyticsRowUiModel>>(emptyList()) }
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    when (content) {
        is AnalyticsContentStateUiModel.Empty,
        is AnalyticsContentStateUiModel.Loading -> {
            // no op
        }

        is AnalyticsContentStateUiModel.WithContent -> {
            LaunchedEffect(screenState.autoScroll, content.rows.size) {
                if (screenState.autoScroll && content.rows.lastIndex != -1) {
                    listState.animateScrollToItem(content.rows.lastIndex)
                }
            }
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
                    analyticsItems = content.items(),
                    onResetClicked = onResetClicked,
                    onItemsChange = {
                        analyticsItems = it
                    },
                )
            },
            actions = {
                FloconOverflow {
                    FloconDropdownMenuItem(
                        text = "Export CSV",
                        leadingIcon = Icons.Outlined.ImportExport,
                        onClick = { onAction(AnalyticsAction.ExportCsv) }
                    )
                    FloconDropdownMenuItem(
                        checked = screenState.autoScroll,
                        text = "Auto scroll",
                        leadingIcon = Icons.Outlined.PlayCircle,
                        onCheckedChange = { onAction(AnalyticsAction.ToggleAutoScroll) }
                    )
                    FloconDropdownMenuItem(
                        checked = screenState.invertList,
                        text = "Invert list",
                        leadingIcon = Icons.AutoMirrored.Outlined.List,
                        onCheckedChange = { onAction(AnalyticsAction.InvertList(it)) }
                    )
                    FloconDropdownSeparator()
                    FloconDropdownMenuItem(
                        text = "Clear old sessions",
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
                    when (content) {
                        is AnalyticsContentStateUiModel.Empty -> {}
                        is AnalyticsContentStateUiModel.Loading -> {}
                        is AnalyticsContentStateUiModel.WithContent -> {
                            itemsIndexed(analyticsItems) { index, item ->
                                AnalyticsRowView(
                                    model = item,
                                    modifier = Modifier.fillMaxWidth(),
                                    onAction = onAction,
                                )
                                if (index != analyticsItems.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.width(dpWidth)
                                    )
                                }
                            }
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
    FloconPanel(
        contentState = selectedItem,
        onClose = { onAction(AnalyticsAction.ClosePanel) },
    ) {
        AnalyticsDetailView(
            modifier = Modifier.fillMaxSize(),
            state = it
        )
    }
}

@Composable
@Preview
private fun AnalyticsScreenPreview() {
    FloconTheme {
        AnalyticsScreen(
            itemsState = previewAnalyticsStateUiModel(),
            onAnalyticsSelected = {},
            onResetClicked = {},
            selectedItem = null,
            content = previewAnalyticsContentStateUiModel(),
            onAction = {},
            modifier = Modifier.fillMaxSize(),
            screenState = previewAnalyticsScreenUiState(),
        )
    }
}
