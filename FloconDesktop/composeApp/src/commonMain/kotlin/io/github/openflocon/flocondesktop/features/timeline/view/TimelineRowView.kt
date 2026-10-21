package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_app_started
import flocondesktop.composeapp.generated.resources.timeline_source_adb
import flocondesktop.composeapp.generated.resources.timeline_source_analytics
import flocondesktop.composeapp.generated.resources.timeline_source_app_start
import flocondesktop.composeapp.generated.resources.timeline_source_crash
import flocondesktop.composeapp.generated.resources.timeline_source_database
import flocondesktop.composeapp.generated.resources.timeline_source_deeplink
import flocondesktop.composeapp.generated.resources.timeline_source_network
import flocondesktop.composeapp.generated.resources.timeline_source_table
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun TimelineSource.label(): StringResource = when (this) {
    TimelineSource.Network -> Res.string.timeline_source_network
    TimelineSource.Database -> Res.string.timeline_source_database
    TimelineSource.Analytics -> Res.string.timeline_source_analytics
    TimelineSource.Crash -> Res.string.timeline_source_crash
    TimelineSource.Table -> Res.string.timeline_source_table
    TimelineSource.Deeplink -> Res.string.timeline_source_deeplink
    TimelineSource.Adb -> Res.string.timeline_source_adb
    TimelineSource.AppStart -> Res.string.timeline_source_app_start
}

// the data of these sources are technical: they are displayed in mono
private val monoTitles = setOf(TimelineSource.Database, TimelineSource.Adb, TimelineSource.Deeplink)
private val monoSubtitles = setOf(TimelineSource.Crash, TimelineSource.Table, TimelineSource.Adb)

/**
 * One event: time and delta | rail | category | content | meta.
 * [delta] is the time since the previous event.
 */
@Composable
internal fun TimelineRowView(
    row: TimelineRowUiModel,
    delta: String?,
    selected: Boolean,
    onClick: (TimelineRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val scale = FloconTheme.scale
    val shape = FloconTheme.shapes.medium
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val (color, soft) = row.source.categoryColors()
    val isCrash = row.source == TimelineSource.Crash
    val highlighted = hovered || selected

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(if (highlighted) colors.bg300 else Color.Transparent, shape)
            // a crash is never missed: the whole row is tinted, with a bar on its left
            .then(
                if (isCrash) {
                    Modifier
                        .background(soft, shape)
                        .drawBehind {
                            drawRoundRect(
                                color = color,
                                size = Size(2.dp.toPx(), size.height),
                                cornerRadius = CornerRadius(2.dp.toPx()),
                            )
                        }
                } else {
                    Modifier
                }
            )
            .then(if (selected) Modifier.border(1.dp, colors.lineStrong, shape) else Modifier)
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick(row) }
            .padding(horizontal = spacing.compact, vertical = spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        TimeColumn(
            time = row.time,
            delta = delta,
            modifier = Modifier.width(TimeColumnWidth * scale),
        )
        Rail(
            color = color,
            haloColor = if (highlighted) colors.bg300 else colors.bg100,
            outerHaloColor = if (isCrash) soft else null,
            // the line goes through the padding and the gap between two rows
            overflow = spacing.tiny + spacing.tiny,
            modifier = Modifier
                .width(RailWidth * scale)
                .fillMaxHeight(),
        )
        Box(modifier = Modifier.width(104.dp * scale)) {
            EventBadge(source = row.source)
        }
        RowContent(
            row = row,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 28.dp * scale),
        )
        RowMeta(row = row)
    }
}

@Composable
private fun TimeColumn(
    time: String,
    delta: String?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(modifier = modifier) {
        Text(
            text = time,
            style = monoStyle(),
            fontWeight = FontWeight.Medium,
            color = colors.ink,
            maxLines = 1,
        )
        if (delta != null) {
            Text(
                text = delta,
                style = monoStyle(small = true),
                color = colors.inkFaint,
                maxLines = 1,
            )
        }
    }
}

// the vertical line of the timeline, with the dot of the category on it
@Composable
private fun Rail(
    color: Color,
    haloColor: Color,
    outerHaloColor: Color?,
    overflow: Dp,
    modifier: Modifier = Modifier,
) {
    val line = FloconTheme.colors.line
    Box(
        modifier = modifier.drawBehind {
            val x = size.width / 2
            drawLine(
                color = line,
                start = Offset(x, -overflow.toPx()),
                end = Offset(x, size.height + overflow.toPx()),
                strokeWidth = 1.dp.toPx(),
            )
        },
        contentAlignment = Alignment.Center,
    ) {
        if (outerHaloColor != null) {
            Box(Modifier.size(22.dp).background(outerHaloColor, CircleShape))
        }
        // the halo hides the line behind the dot
        Box(Modifier.size(16.dp).background(haloColor, CircleShape), contentAlignment = Alignment.Center) {
            Box(Modifier.size(10.dp).background(color, CircleShape))
        }
    }
}

@Composable
private fun RowContent(
    row: TimelineRowUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val isCrash = row.source == TimelineSource.Crash
    // the status and the provider are displayed on the right
    val subtitle = row.subtitle.takeIf { row.network == null && row.source != TimelineSource.Analytics }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        val network = row.network
        if (network != null) {
            NetworkTitle(network)
        } else {
            val base = FloconTheme.typography.bodyMedium
            Text(
                text = if (row.source == TimelineSource.AppStart) stringResource(Res.string.timeline_app_started) else row.title,
                style = if (row.source in monoTitles) base.copy(fontFamily = FontFamily.Monospace) else base,
                fontWeight = FontWeight.Medium,
                color = if (isCrash) colors.catCrash else colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (subtitle != null) {
            val base = FloconTheme.typography.labelMedium
            Text(
                text = subtitle,
                style = if (row.source in monoSubtitles) base.copy(fontFamily = FontFamily.Monospace) else base,
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NetworkTitle(network: TimelineNetworkUiModel) {
    val colors = FloconTheme.colors
    val title: AnnotatedString = buildAnnotatedString {
        if (network.host != null) {
            withStyle(SpanStyle(color = colors.inkMuted, fontWeight = FontWeight.Normal)) { append(network.host) }
            append(" ")
        }
        withStyle(SpanStyle(color = colors.ink, fontWeight = FontWeight.Medium)) { append(network.path) }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (network.method.isNotEmpty()) {
            MethodPill(method = network.method)
        }
        Text(
            text = title,
            style = FloconTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

@Composable
private fun RowMeta(row: TimelineRowUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        val network = row.network
        if (network != null) {
            network.duration?.let {
                Text(
                    text = it,
                    style = monoStyle(small = true),
                    color = FloconTheme.colors.inkFaint,
                    maxLines = 1,
                )
            }
            if (network.statusCode != null) {
                StatusPill(code = network.statusCode)
            } else {
                // no http code (ex: gRPC): what the app reports, if anything
                row.subtitle?.let { TagPill(text = it) }
            }
        } else if (row.source == TimelineSource.Analytics) {
            row.subtitle?.let { TagPill(text = it) }
        }
    }
}
