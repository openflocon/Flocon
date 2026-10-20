package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.timeline.mapper.formatDelta
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.SessionHeaderView
import io.github.openflocon.flocondesktop.features.timeline.view.SourceChip
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineHeaderView
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineRowView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
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

    TimelineContent(
        modifier = modifier.fillMaxSize(),
        rows = rows,
        filterText = filterText,
        selectedSources = selectedSources,
        selectedKey = selectedKey,
        summary = summary,
        isLive = isLive,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TimelineContent(
    rows: LazyPagingItems<TimelineItemUiModel>,
    filterText: State<String>,
    selectedSources: Set<TimelineSource>,
    selectedKey: String?,
    summary: TimelineSummaryUiModel,
    isLive: Boolean,
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

    FloconFeature(
        modifier = modifier
            .background(colors.bg100, panelShape)
            .border(1.dp, colors.line, panelShape)
            .padding(FloconTheme.spacing.large)
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
        TimelineHeaderView(
            summary = summary,
            isLive = isLive,
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
                    .width(280.dp * FloconTheme.scale)
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

        if (rows.itemCount == 0) {
            Text(
                text = stringResource(Res.string.timeline_empty),
                style = FloconTheme.typography.bodyMedium,
                color = colors.inkMuted,
                modifier = Modifier.padding(FloconTheme.spacing.large),
            )
        }

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
        ) {
            items(
                count = rows.itemCount,
                key = rows.itemKey { it.key },
            ) { index ->
                when (val item = rows[index]) {
                    is TimelineItemUiModel.Row -> {
                        val olderTimestamp = olderTimestamp(rows, index)
                        val delta = remember(item.row.timestamp, olderTimestamp) {
                            olderTimestamp?.let { formatDelta(item.row.timestamp - it) }
                        }
                        TimelineRowView(
                            row = item.row,
                            delta = delta,
                            selected = item.row.key == selectedKey,
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

                    is TimelineItemUiModel.SessionHeader -> SessionHeaderView(
                        header = item,
                        eventCount = summary.sessionEventCounts[item.sessionStart],
                    )

                    null -> Unit
                }
            }
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
