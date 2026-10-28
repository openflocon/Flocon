package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.timeline_empty
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.timeline.mapper.adjacentCallIndex
import io.github.openflocon.flocondesktop.features.timeline.mapper.formatDelta
import io.github.openflocon.flocondesktop.features.timeline.mapper.linkKey
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineItems
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.CallRail
import io.github.openflocon.flocondesktop.features.timeline.view.SessionHeader
import io.github.openflocon.flocondesktop.features.timeline.view.SourceChip
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineHeader
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRow
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimelineScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: TimelineViewModel = koinViewModel()
    val rows = viewModel.items.collectAsLazyPagingItems()
    val filterText = viewModel.filterText.collectAsStateWithLifecycle()
    val selectedSources by viewModel.selectedSources.collectAsStateWithLifecycle()
    val selectedKey by viewModel.selectedKey.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val isLive by viewModel.isLive.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val showEmptyState by viewModel.showEmptyState.collectAsStateWithLifecycle()

    TimelineContent(
        modifier = modifier.fillMaxSize(),
        rows = rows,
        filterText = filterText,
        selectedSources = selectedSources,
        selectedKey = selectedKey,
        summary = summary,
        isLive = isLive,
        settings = settings,
        showEmptyState = showEmptyState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun TimelineContent(
    rows: LazyPagingItems<TimelineItemUiModel>,
    filterText: State<String>,
    selectedSources: Set<TimelineSource>,
    selectedKey: String?,
    summary: TimelineSummaryUiModel,
    isLive: Boolean,
    settings: TimelineSettingsUiModel,
    showEmptyState: Boolean,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val panelShape = FloconTheme.shapes.large

    // Ctrl+K (Cmd+K on macOS) goes to the search field, wherever the focus is in the screen
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var searchFocused by remember { mutableStateOf(false) }
    val screenFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { screenFocus.requestFocus() }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
            .background(colors.bg100, panelShape)
            .border(1.dp, colors.line, panelShape)
            .mascotPlatform()
            .padding(start = FloconTheme.spacing.large, top = FloconTheme.spacing.large, end = FloconTheme.spacing.large)
            .focusRequester(screenFocus)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    false
                } else if (event.key == Key.K && (event.isCtrlPressed || event.isMetaPressed)) {
                    runCatching { searchFocus.requestFocus() }
                    true
                } else if (!searchFocused && !event.isCtrlPressed && !event.isMetaPressed && !event.isAltPressed) {
                    // up / down / enter move the selection, but the search field keeps its own keys
                    handleSelectionKey(
                        key = event.key,
                        rows = rows,
                        selectedKey = selectedKey,
                        inverted = settings.invertList,
                        listState = listState,
                        scope = scope,
                        onAction = onAction,
                    )
                } else {
                    false
                }
            },
    ) {
        TimelineHeader(
            summary = summary,
            isLive = isLive,
            settings = settings,
            onAction = onAction,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FilterBar(
                filterText = filterText,
                placeholderText = stringResource(Res.string.filter),
                modifier = Modifier
                    .width(FloconTheme.sizes.sidePanelWidth)
                    .focusRequester(searchFocus)
                    .onFocusChanged { searchFocused = it.isFocused },
                containerColor = colors.bg200,
                onTextChange = { onAction(TimelineAction.FilterTextChanged(it)) },
            )
            TimelineSource.entries.forEach { source ->
                SourceChip(
                    source = source,
                    count = summary.sourceCounts[source] ?: 0,
                    selected = source in selectedSources,
                    onClick = { onAction(TimelineAction.ToggleSource(source)) },
                )
            }
        }

        if (showEmptyState) {
            FeatureEmptyState(
                doc = FeatureDoc.Timeline,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (rows.itemCount == 0) {
            Text(
                text = stringResource(Res.string.timeline_empty),
                style = FloconTheme.typography.bodyMedium,
                color = colors.inkMuted,
                modifier = Modifier.padding(FloconTheme.spacing.large),
            )
        }

        // the rows currently hovered, by key: a request and its response are highlighted together
        val hoveredRows = remember { mutableStateMapOf<String, TimelineRowUiModel>() }
        // the links only show while the pointer is over the list: a row can miss its exit, ex: when the detail panel opens
        val listInteraction = remember { MutableInteractionSource() }
        val listHovered by listInteraction.collectIsHoveredAsState()
        LaunchedEffect(listHovered) { if (!listHovered) hoveredRows.clear() }
        val hoveredNow = if (listHovered) hoveredRows.values.toList() else emptyList()
        val hoveredCallKeys = hoveredNow.mapTo(mutableSetOf()) { it.callKey }
        val hoveredLinkKeys = hoveredNow.mapNotNullTo(mutableSetOf()) { it.linkKey() }

        // the newest event is the first of the list, whatever the direction: look at it, not at the count, so the older
        // pages loaded while scrolling do not bring the list back to the top
        val newestKey = if (rows.itemCount > 0) rows.peek(0)?.key else null
        LaunchedEffect(settings.autoScroll, newestKey) {
            if (settings.autoScroll && newestKey != null) {
                listState.animateScrollToItem(0)
            }
        }
        // the bar between the lines of a hovered network call, nothing to link while it waits for its response, or between
        // the same event repeated
        val railRow = hoveredNow.firstOrNull {
            it.source == TimelineSource.Network && (it.phase == TimelinePhase.Response || it.network?.duration != null)
        } ?: hoveredNow.firstOrNull { it.linkKey() != null }
        val railLinkKey = railRow?.linkKey()
        val railCallKey = railRow?.callKey.takeIf { railRow?.source == TimelineSource.Network }
        val itemCount = rows.itemCount
        val linkedIndices = remember(railLinkKey, railCallKey, itemCount) {
            (0 until itemCount).filter { index ->
                val row = (rows.peek(index) as? TimelineItemUiModel.Row)?.row ?: return@filter false
                if (railCallKey != null) row.callKey == railCallKey else row.linkKey() == railLinkKey
            }
        }

        Box(modifier = Modifier.fillMaxSize().hoverable(listInteraction)) {
            LazyColumn(
                state = listState,
                reverseLayout = settings.invertList,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = FloconTheme.spacing.large),
            ) {
                items(
                    count = rows.itemCount,
                    key = rows.itemKey { it.key },
                ) { index ->
                    when (val item = rows[index]) {
                        is TimelineItemUiModel.Row -> if (item.row.source == TimelineSource.AppStart) {
                            // the app start of a session, at the time the app started
                            SessionHeader(
                                row = item.row,
                                eventCount = summary.sessionEventCounts[item.row.timestamp],
                            )
                        } else {
                            val olderTimestamp = olderTimestamp(rows, index)
                            val delta = remember(item.row.timestamp, olderTimestamp) {
                                olderTimestamp?.let { formatDelta(item.row.timestamp - it) }
                            }
                            TimelineRow(
                                row = item.row,
                                delta = delta,
                                selected = item.row.callKey == selectedKey,
                                hovered = item.row.callKey in hoveredCallKeys || item.row.linkKey() in hoveredLinkKeys,
                                onHoverChange = { hovered ->
                                    if (hovered) hoveredRows[item.row.key] = item.row else hoveredRows.remove(item.row.key)
                                },
                                onClick = { onAction(TimelineAction.Open(it)) },
                                highlight = filterText.value,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        is TimelineItemUiModel.DayHeader -> Text(
                            text = item.day.uppercase(),
                            style = FloconTheme.typography.labelSmall,
                            color = colors.inkFaint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = FloconTheme.spacing.medium, top = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.extraSmall),
                        )

                        null -> Unit
                    }
                }
            }
            CallRail(
                listState = listState,
                linkedIndices = linkedIndices,
                // the request of a call can be older than the events loaded
                leavesAfterLoaded = railCallKey != null && linkedIndices.size < 2,
                color = railRow?.source?.categoryColors()?.first ?: colors.catNetwork,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * The time of the event just below (the previous one, as the list starts from the newest). The headers are skipped.
 * Only the items already loaded are looked at: a delta never triggers a page load.
 */
private fun olderTimestamp(rows: LazyPagingItems<TimelineItemUiModel>, index: Int): Long? {
    for (next in index + 1..minOf(index + 2, rows.itemCount - 1)) {
        val row = (rows.peek(next) as? TimelineItemUiModel.Row)?.row
        if (row != null) return row.timestamp
    }
    return null
}

/**
 * Up and down select the call above or below the selected one, enter opens it again.
 * The list goes down from the newest, the other way round when it is [inverted]. True when the key was used.
 */
private fun handleSelectionKey(
    key: Key,
    rows: LazyPagingItems<TimelineItemUiModel>,
    selectedKey: String?,
    inverted: Boolean,
    listState: LazyListState,
    scope: CoroutineScope,
    onAction: (TimelineAction) -> Unit,
): Boolean {
    val keyAt = { index: Int -> (rows.peek(index) as? TimelineItemUiModel.Row)?.row?.takeIf { it.toDestination() != null }?.callKey }
    val target = when (key) {
        Key.DirectionDown -> adjacentCallIndex(rows.itemCount, selectedKey, if (inverted) -1 else 1, keyAt)
        Key.DirectionUp -> adjacentCallIndex(rows.itemCount, selectedKey, if (inverted) 1 else -1, keyAt)
        Key.Enter, Key.NumPadEnter -> (0 until rows.itemCount).firstOrNull { selectedKey != null && keyAt(it) == selectedKey }
        else -> return false
    } ?: return true
    val row = (rows.peek(target) as? TimelineItemUiModel.Row)?.row ?: return true
    onAction(TimelineAction.Open(row))
    if (listState.layoutInfo.visibleItemsInfo.none { it.index == target }) {
        scope.launch { listState.scrollToItem(target) }
    }
    return true
}

@Composable
private fun TimelineScreenPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val items = remember { previewTimelineItems() }
        val rows = remember { MutableStateFlow(PagingData.from(items)) }.collectAsLazyPagingItems()
        val selected = remember { items.filterIsInstance<TimelineItemUiModel.Row>().first { it.row.source == TimelineSource.Database }.row.callKey }

        TimelineContent(
            rows = rows,
            filterText = remember { mutableStateOf("") },
            selectedSources = TimelineSource.entries.toSet(),
            selectedKey = selected,
            summary = previewTimelineSummaryUiModel(),
            isLive = true,
            settings = previewTimelineSettingsUiModel(),
            showEmptyState = false,
            onAction = {},
            modifier = Modifier.height(PreviewScreenHeight),
        )
    }
}

private val PreviewScreenHeight = 900.dp

@Preview
@Composable
private fun TimelineScreenDarkPreview() = TimelineScreenPreviewContent(isDarkTheme = true)

@Preview
@Composable
private fun TimelineScreenLightPreview() = TimelineScreenPreviewContent(isDarkTheme = false)
