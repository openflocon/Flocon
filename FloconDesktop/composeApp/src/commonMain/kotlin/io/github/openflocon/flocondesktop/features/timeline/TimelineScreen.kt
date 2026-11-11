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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.ScatterPlot
import androidx.compose.material.icons.outlined.Timeline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.timeline_empty
import flocondesktop.composeapp.generated.resources.timeline_filters_toggle
import flocondesktop.composeapp.generated.resources.timeline_graph_lanes
import flocondesktop.composeapp.generated.resources.timeline_graph_single
import flocondesktop.composeapp.generated.resources.timeline_view_graph
import flocondesktop.composeapp.generated.resources.timeline_view_list
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphFocusUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphSessionUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.PreviewSessionDurationMs
import io.github.openflocon.flocondesktop.features.timeline.graph.TimelineGraph
import io.github.openflocon.flocondesktop.features.timeline.graph.buildGraphUiModel
import io.github.openflocon.flocondesktop.features.timeline.graph.previewGraphEvents
import io.github.openflocon.flocondesktop.features.timeline.mapper.adjacentCallIndex
import io.github.openflocon.flocondesktop.features.timeline.mapper.branch
import io.github.openflocon.flocondesktop.features.timeline.mapper.formatDelta
import io.github.openflocon.flocondesktop.features.timeline.mapper.linkKey
import io.github.openflocon.flocondesktop.features.timeline.mapper.linkedRowIndices
import io.github.openflocon.flocondesktop.features.timeline.mapper.screenshotScreenName
import io.github.openflocon.flocondesktop.features.timeline.mapper.screenshotSpanIndices
import io.github.openflocon.flocondesktop.features.timeline.mapper.toDestination
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotContextUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotsStatus
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineItems
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.CallRail
import io.github.openflocon.flocondesktop.features.timeline.view.ScreenshotMarkerRow
import io.github.openflocon.flocondesktop.features.timeline.view.SessionHeader
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineHeader
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRow
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRowMenu
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRowMenuCallbacks
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineTransactionRow
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.flocondesktop.features.timeline.view.transactionBranch
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconOption
import io.github.openflocon.library.designsystem.components.FloconSegmentedIconToggle
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.collections.immutable.persistentListOf
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
    val selectedKey by viewModel.selectedKey.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val isLive by viewModel.isLive.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val screenshotsStatus by viewModel.screenshotsStatus.collectAsStateWithLifecycle()
    val screenshotContext by viewModel.screenshotContext.collectAsStateWithLifecycle()
    val showEmptyState by viewModel.showEmptyState.collectAsStateWithLifecycle()
    val filterPanelOpen by viewModel.filterPanelOpen.collectAsStateWithLifecycle()
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
    val graph by viewModel.graph.collectAsStateWithLifecycle()
    val graphSession by viewModel.graphSessionPosition.collectAsStateWithLifecycle()
    val graphFocus by viewModel.graphFocus.collectAsStateWithLifecycle()

    TimelineContent(
        modifier = modifier.fillMaxSize(),
        rows = rows,
        filterText = filterText,
        selectedKey = selectedKey,
        summary = summary,
        isLive = isLive,
        settings = settings,
        screenshotsStatus = screenshotsStatus,
        screenshotContext = screenshotContext,
        showEmptyState = showEmptyState,
        filterPanelOpen = filterPanelOpen,
        activeFilterCount = activeFilterCount,
        graph = graph,
        graphSession = graphSession,
        graphFocus = graphFocus,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun TimelineContent(
    rows: LazyPagingItems<TimelineItemUiModel>,
    filterText: State<String>,
    selectedKey: String?,
    summary: TimelineSummaryUiModel,
    isLive: Boolean,
    settings: TimelineSettingsUiModel,
    screenshotsStatus: TimelineScreenshotsStatus,
    showEmptyState: Boolean,
    filterPanelOpen: Boolean,
    activeFilterCount: Int,
    graph: GraphUiModel,
    graphSession: GraphSessionUiModel?,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
    // a zone of the graph asked from outside, shown once
    graphFocus: GraphFocusUiModel? = null,
    // the app and the device shown beside the screenshots
    screenshotContext: TimelineScreenshotContextUiModel? = null,
) {
    val colors = FloconTheme.colors
    val panelShape = FloconTheme.shapes.large

    // Ctrl+K (Cmd+K on macOS) goes to the search field, wherever the focus is in the screen
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var searchFocused by remember { mutableStateOf(false) }
    // the zoom field of the graph: its keys are not the ones of the list
    var graphFieldFocused by remember { mutableStateOf(false) }
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
                } else if (event.key == Key.C && event.isShiftPressed && (event.isCtrlPressed || event.isMetaPressed) &&
                    selectedKey?.startsWith("${TimelineSource.Network.name}:") == true
                ) {
                    // the shortcut shown in the menu of a network call
                    onAction(TimelineAction.CopySelectedUrl)
                    true
                } else if (event.key == Key.K && (event.isCtrlPressed || event.isMetaPressed)) {
                    runCatching { searchFocus.requestFocus() }
                    true
                } else if (!searchFocused && !graphFieldFocused && !event.isCtrlPressed && !event.isMetaPressed && !event.isAltPressed) {
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
            screenshotsStatus = screenshotsStatus,
            onAction = onAction,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconButton(
                onClick = { onAction(TimelineAction.ToggleFilterPanel) },
                containerColor = if (filterPanelOpen) colors.accentSoft else colors.bg200,
                modifier = Modifier.height(FloconTheme.sizes.controlHeight),
            ) {
                FloconIcon(imageVector = Icons.Outlined.FilterList)
                Text(
                    text = stringResource(Res.string.timeline_filters_toggle),
                    style = FloconTheme.typography.labelLarge,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = FloconTheme.spacing.extraSmall),
                )
                if (activeFilterCount > 0) {
                    Text(
                        text = activeFilterCount.toString(),
                        style = FloconTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accent,
                    )
                }
            }
            FilterBar(
                filterText = filterText,
                placeholderText = stringResource(Res.string.filter),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(searchFocus)
                    .onFocusChanged { searchFocused = it.isFocused },
                containerColor = colors.bg200,
                onTextChange = { onAction(TimelineAction.FilterTextChanged(it)) },
            )
            // how the graph groups the events, it means nothing to the list
            if (settings.graph) {
                val lanesTooltip = stringResource(Res.string.timeline_graph_lanes)
                val singleTooltip = stringResource(Res.string.timeline_graph_single)
                FloconSegmentedIconToggle(
                    options = remember(lanesTooltip, singleTooltip) {
                        persistentListOf(
                            FloconSegmentedIconOption(icon = Icons.Outlined.Reorder, tooltip = lanesTooltip),
                            FloconSegmentedIconOption(icon = Icons.Outlined.ScatterPlot, tooltip = singleTooltip),
                        )
                    },
                    selectedIndex = if (settings.mergePlugins) 1 else 0,
                    onSelect = { onAction(TimelineAction.SetMergePlugins(it == 1)) },
                )
            }
            // the same switch as the files: the list or the graph of the session
            val listTooltip = stringResource(Res.string.timeline_view_list)
            val graphTooltip = stringResource(Res.string.timeline_view_graph)
            FloconSegmentedIconToggle(
                options = remember(listTooltip, graphTooltip) {
                    persistentListOf(
                        FloconSegmentedIconOption(icon = Icons.AutoMirrored.Outlined.List, tooltip = listTooltip),
                        FloconSegmentedIconOption(icon = Icons.Outlined.Timeline, tooltip = graphTooltip),
                    )
                },
                selectedIndex = if (settings.graph) 1 else 0,
                onSelect = { onAction(TimelineAction.SetGraph(it == 1)) },
            )
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
        // the screenshot hovered, by key: the bar shows the events it covers
        var hoveredScreenshotKey by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(listHovered) {
            if (!listHovered) {
                hoveredRows.clear()
                hoveredScreenshotKey = null
            }
        }
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
        val screenshotIndices = remember(hoveredScreenshotKey, itemCount) {
            hoveredScreenshotKey?.let { key ->
                (0 until itemCount).firstOrNull { rows.peek(it)?.key == key }?.let { shotIndex ->
                    screenshotSpanIndices(shotIndex) { rows.peek(it) }
                }
            }
        }
        val linkedIndices = screenshotIndices ?: remember(railLinkKey, railCallKey, itemCount) {
            linkedRowIndices(itemCount, railCallKey, railLinkKey) { (rows.peek(it) as? TimelineItemUiModel.Row)?.row }
        }

        if (settings.graph) {
            // a click opens the event in the detail panel, like a click on a row of the list
            TimelineGraph(
                model = graph,
                session = graphSession,
                selectedKey = selectedKey,
                onSelect = { onAction(TimelineAction.Open(it)) },
                onPreviousSession = { onAction(TimelineAction.MoveGraphSession(-1)) },
                onNextSession = { onAction(TimelineAction.MoveGraphSession(1)) },
                onEditingChange = { graphFieldFocused = it },
                autoScroll = settings.autoScroll,
                onAutoScrollChange = { onAction(TimelineAction.ToggleAutoScroll(it)) },
                screenshotsEnabled = settings.screenshots,
                screenshotsAvailable = screenshotsStatus != TimelineScreenshotsStatus.Unavailable,
                onTurnOnScreenshots = { onAction(TimelineAction.SetScreenshots(true)) },
                clusterEvents = settings.clusterEvents,
                focus = graphFocus,
                onFocusApplied = { onAction(TimelineAction.GraphFocusApplied) },
                modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = FloconTheme.spacing.large),
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().hoverable(listInteraction)) {
                LazyColumn(
                    state = listState,
                    reverseLayout = settings.invertList,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = FloconTheme.spacing.large),
                ) {
                    items(
                        count = rows.itemCount,
                        key = { index -> uniqueItemKey(rows, index) },
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
                                TimelineRowMenu(
                                    row = item.row,
                                    callbacks = TimelineRowMenuCallbacks(
                                        onNetworkOption = { onAction(TimelineAction.NetworkOption(item.row.id, it)) },
                                        onExclude = { facet, value -> onAction(TimelineAction.Exclude(facet, value)) },
                                        onExcludePath = { onAction(TimelineAction.ExcludePath(it)) },
                                        onExcludeSource = { onAction(TimelineAction.ExcludeSource(item.row.source)) },
                                        onManageExclusions = { onAction(TimelineAction.OpenFilters(item.row.source)) },
                                    ),
                                ) {
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
                                        // the queries of a transaction hang from its header
                                        modifier = Modifier.fillMaxWidth().then(
                                            item.branch()?.let { (transactionId, level) ->
                                                Modifier.transactionBranch(
                                                    isLast = isLastOfTransaction(rows, index, settings.invertList, transactionId, level),
                                                    level = level,
                                                )
                                            } ?: Modifier,
                                        ),
                                    )
                                }
                            }

                            is TimelineItemUiModel.TransactionHeader -> TimelineTransactionRow(
                                item = item,
                                onClick = { onAction(TimelineAction.ToggleTransaction(item.foldingId)) },
                                // the header of a nested transaction hangs from the transaction that holds it
                                modifier = item.branch()?.let { (transactionId, level) ->
                                    Modifier.transactionBranch(
                                        isLast = isLastOfTransaction(rows, index, settings.invertList, transactionId, level),
                                        level = level,
                                    )
                                } ?: Modifier,
                            )

                            is TimelineItemUiModel.Screenshot -> ScreenshotMarkerRow(
                                item = item,
                                screenName = remember(index, rows.itemCount) { screenshotScreenName(index, rows.itemCount) { rows.peek(it) } },
                                context = screenshotContext,
                                onHoverChange = { hovered ->
                                    if (hovered) {
                                        hoveredScreenshotKey = item.key
                                    } else if (hoveredScreenshotKey == item.key) {
                                        hoveredScreenshotKey = null
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )

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
                    leavesAfterLoaded = screenshotIndices == null && railCallKey != null && linkedIndices.size < 2,
                    color = if (screenshotIndices != null) colors.accent else railRow?.source?.categoryColors()?.first ?: colors.catNetwork,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

/**
 * The key of the item, unique in the list. Two pages loaded from different snapshots of the table (a response arrives between
 * them) can both hold the same event, and a duplicated key crashes the LazyColumn: the second one gets its index appended.
 */
private fun uniqueItemKey(rows: LazyPagingItems<TimelineItemUiModel>, index: Int): Any {
    val key = rows.peek(index)?.key ?: return "placeholder:$index"
    return if (index > 0 && rows.peek(index - 1)?.key == key) "$key#$index" else key
}

/**
 * The query is the last one of its transaction when the item displayed below it is not another query of it: the next one in the list,
 * the previous one when the list is [inverted]. Only the items already loaded are looked at.
 */
private fun isLastOfTransaction(
    rows: LazyPagingItems<TimelineItemUiModel>,
    index: Int,
    inverted: Boolean,
    transactionId: String,
    level: Int,
): Boolean {
    val below = if (inverted) index - 1 else index + 1
    if (below !in 0 until rows.itemCount) return true
    // the line goes on while what comes next hangs from the same transaction, at this level or deeper
    val next = rows.peek(below).branch()
    return next == null || next.first != transactionId || next.second < level
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
            selectedKey = selected,
            summary = previewTimelineSummaryUiModel(),
            isLive = true,
            settings = previewTimelineSettingsUiModel(),
            screenshotsStatus = TimelineScreenshotsStatus.Working,
            showEmptyState = false,
            filterPanelOpen = false,
            activeFilterCount = 2,
            graph = remember { buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs, hasAppStart = true) },
            graphSession = GraphSessionUiModel(position = 1, total = 1),
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
