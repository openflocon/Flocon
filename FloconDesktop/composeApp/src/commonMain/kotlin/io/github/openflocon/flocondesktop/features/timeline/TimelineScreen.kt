package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import io.github.openflocon.flocondesktop.features.timeline.mapper.formatDelta
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.CallRail
import io.github.openflocon.flocondesktop.features.timeline.view.SessionHeader
import io.github.openflocon.flocondesktop.features.timeline.view.SourceChip
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineHeader
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRow
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.navigation.mascot.mascotPlatform
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
                if (event.type == KeyEventType.KeyDown && event.key == Key.K && (event.isCtrlPressed || event.isMetaPressed)) {
                    runCatching { searchFocus.requestFocus() }
                    true
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
                    .focusRequester(searchFocus),
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
        val hoveredCallKeys = hoveredRows.values.mapTo(mutableSetOf()) { it.callKey }

        val listState = rememberLazyListState()

        // the newest event is the first of the list, whatever the direction: look at it, not at the count, so the older
        // pages loaded while scrolling do not bring the list back to the top
        val newestKey = if (rows.itemCount > 0) rows.peek(0)?.key else null
        LaunchedEffect(settings.autoScroll, newestKey) {
            if (settings.autoScroll && newestKey != null) {
                listState.animateScrollToItem(0)
            }
        }
        // the bar between the two lines of a hovered network call: nothing to link while it waits for its response
        val railCall = hoveredRows.values.firstOrNull {
            it.source == TimelineSource.Network && (it.phase == TimelinePhase.Response || it.network?.duration != null)
        }

        Box(modifier = Modifier.fillMaxSize()) {
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
                                hovered = item.row.callKey in hoveredCallKeys,
                                onHoverChange = { hovered ->
                                    if (hovered) hoveredRows[item.row.key] = item.row else hoveredRows.remove(item.row.key)
                                },
                                onClick = { onAction(TimelineAction.Open(it)) },
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
                // the response is the upper line of the list, sorted from the newest, the lower one when the list is inverted
                upperKey = if (settings.invertList) railCall?.callKey else railCall?.let { "${it.callKey}:${TimelinePhase.Response.name}" },
                lowerKey = if (settings.invertList) railCall?.let { "${it.callKey}:${TimelinePhase.Response.name}" } else railCall?.callKey,
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
