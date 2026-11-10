package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_cluster_events_count
import flocondesktop.composeapp.generated.resources.timeline_cluster_expand
import flocondesktop.composeapp.generated.resources.timeline_cluster_zoom
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconFilterChip
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.stringResource

internal const val ClusterPopoverWidthPx = 340f

private val SourceDotSize = 8.dp
private const val MaxVisibleRows = 6.5f
private const val RelativeTimeWeight = 0.16f
private const val StatusWeight = 0.1f

/**
 * The events of a group that belong to one of the [activeLanes], in their order (by start).
 */
internal fun filterClusterEvents(events: List<GraphEventUiModel>, activeLanes: Set<GraphLane>): List<GraphEventUiModel> = events.filter { it.lane in activeLanes }

/**
 * What a group of close events holds, opened by a click on its marker: the number of events and the time it spans, one chip per
 * source to filter the list, every event of the group (each one can be opened) and the buttons that zoom on the group or open it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GraphClusterPopover(
    cluster: GraphClusterUiModel,
    laneColors: Map<GraphLane, Color>,
    laneLabels: Map<GraphLane, String>,
    onZoom: () -> Unit,
    onExpand: () -> Unit,
    onSelectEvent: (GraphEventUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    var activeLanes by remember(cluster.key) { mutableStateOf(cluster.counts.map { it.first }.toSet()) }
    val events = remember(cluster.key, activeLanes) { filterClusterEvents(cluster.events, activeLanes) }
    Column(
        modifier = modifier
            .width(ClusterPopoverWidthPx.dp)
            .clip(FloconTheme.shapes.medium)
            .background(colors.bg100)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            // the clicks on the popover are not for the graph under it
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.timeline_cluster_events_count, cluster.size),
                style = FloconTheme.typography.labelLarge,
                color = colors.ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatGraphDuration(cluster.endMs - cluster.startMs),
                style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = colors.inkMuted,
            )
        }
        Text(
            text = if (cluster.endMs > cluster.startMs) "${formatGraphStart(cluster.startMs)} - ${formatGraphStart(cluster.endMs)}" else formatGraphStart(cluster.startMs),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkMuted,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            cluster.counts.forEach { (lane, count) ->
                FloconFilterChip(
                    text = laneLabels.getValue(lane),
                    selected = lane in activeLanes,
                    onClick = { activeLanes = if (lane in activeLanes) activeLanes - lane else activeLanes + lane },
                    count = count,
                    leading = { Box(modifier = Modifier.size(SourceDotSize).background(laneColors.getValue(lane))) },
                )
            }
        }
        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxWidth().heightIn(max = FloconTheme.sizes.rowHeight * MaxVisibleRows)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(end = FloconTheme.spacing.small),
            ) {
                itemsIndexed(events, key = { _, event -> event.key }) { index, event ->
                    ClusterEventRow(
                        event = event,
                        relativeMs = event.startMs - cluster.startMs,
                        laneColor = laneColors.getValue(event.lane),
                        laneLabel = laneLabels.getValue(event.lane),
                        striped = index % 2 == 1,
                        onClick = { onSelectEvent(event) },
                    )
                }
            }
            FloconVerticalScrollbar(
                adapter = rememberFloconScrollbarAdapter(listState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)) {
            FloconButton(onClick = onZoom) { Text(stringResource(Res.string.timeline_cluster_zoom)) }
            FloconButton(onClick = onExpand) { Text(stringResource(Res.string.timeline_cluster_expand)) }
        }
    }
}

@Composable
private fun ClusterEventRow(
    event: GraphEventUiModel,
    relativeMs: Long,
    laneColor: Color,
    laneLabel: String,
    striped: Boolean,
    onClick: () -> Unit,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = FloconTheme.sizes.rowHeight)
            .background(if (striped) colors.bg200 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = formatGraphStart(relativeMs),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkMuted,
            maxLines = 1,
            modifier = Modifier.weight(RelativeTimeWeight),
        )
        Box(modifier = Modifier.size(SourceDotSize).background(laneColor))
        Text(
            text = event.method ?: laneLabel,
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkMuted,
            maxLines = 1,
        )
        Text(
            text = event.name,
            style = FloconTheme.typography.labelMedium,
            color = colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Box(modifier = Modifier.weight(StatusWeight), contentAlignment = Alignment.CenterEnd) {
            event.statusCode?.let { status ->
                Text(
                    text = status.toString(),
                    style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                    color = if (event.isError) colors.statusError else colors.statusSuccess,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun GraphClusterPopoverPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val events = previewGraphEvents()
        val cluster = clusterEvents(events, pxPerMs = 0.01).clusters.first()
        GraphClusterPopover(
            cluster = cluster,
            laneColors = GraphLane.entries.associateWith { it.source.categoryColors().first },
            laneLabels = GraphLane.entries.associateWith { it.name },
            onZoom = {},
            onExpand = {},
            onSelectEvent = {},
        )
    }
}

@Preview
@Composable
private fun GraphClusterPopoverDarkPreview() = GraphClusterPopoverPreviewContent(isDarkTheme = true)

@Preview
@Composable
private fun GraphClusterPopoverLightPreview() = GraphClusterPopoverPreviewContent(isDarkTheme = false)
