package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import flocondesktop.composeapp.generated.resources.timeline_cluster_more
import flocondesktop.composeapp.generated.resources.timeline_cluster_zoom
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import org.jetbrains.compose.resources.stringResource

internal const val ClusterPopoverWidthPx = 240f

// the events listed when the zoom cannot separate them
private const val MaxListedEvents = 8
private val SourceDotSize = 10.dp

/**
 * What a group of close events holds, opened by a click on its marker: the number of events per source, the time it spans,
 * and a button that zooms on it. Events that no zoom can separate (the same instant) are listed, each one can be opened.
 */
@Composable
internal fun GraphClusterPopover(
    cluster: GraphClusterUiModel,
    laneColors: Map<GraphLane, Color>,
    laneLabels: Map<GraphLane, String>,
    onZoom: () -> Unit,
    onSelectEvent: (GraphEventUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
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
        Text(
            text = stringResource(Res.string.timeline_cluster_events_count, cluster.size),
            style = FloconTheme.typography.labelLarge,
            color = colors.ink,
        )
        Text(
            text = if (cluster.endMs > cluster.startMs) "${formatGraphStart(cluster.startMs)} - ${formatGraphStart(cluster.endMs)}" else formatGraphStart(cluster.startMs),
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkMuted,
        )
        cluster.counts.forEach { (lane, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Box(modifier = Modifier.size(SourceDotSize).clip(CircleShape).background(laneColors.getValue(lane)))
                Text(
                    text = laneLabels.getValue(lane),
                    style = FloconTheme.typography.labelMedium,
                    color = colors.ink,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = count.toString(),
                    style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                    color = colors.inkMuted,
                )
            }
        }
        // a zoom separates events that are at least a few px apart at the highest zoom
        if ((cluster.endMs - cluster.startMs) * MAX_PX_PER_MS < ClusterDistancePx) {
            cluster.events.take(MaxListedEvents).forEach { event ->
                Text(
                    text = event.name,
                    style = FloconTheme.typography.labelMedium,
                    color = colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().clickable { onSelectEvent(event) },
                )
            }
            if (cluster.size > MaxListedEvents) {
                Text(
                    text = stringResource(Res.string.timeline_cluster_more, cluster.size - MaxListedEvents),
                    style = FloconTheme.typography.labelSmall,
                    color = colors.inkFaint,
                )
            }
        }
        FloconButton(onClick = onZoom) { Text(stringResource(Res.string.timeline_cluster_zoom)) }
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
