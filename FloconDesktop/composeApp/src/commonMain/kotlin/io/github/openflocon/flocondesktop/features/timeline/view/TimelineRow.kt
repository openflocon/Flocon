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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_app_started
import flocondesktop.composeapp.generated.resources.timeline_copy
import flocondesktop.composeapp.generated.resources.timeline_source_accessibility
import flocondesktop.composeapp.generated.resources.timeline_source_adb
import flocondesktop.composeapp.generated.resources.timeline_source_analytics
import flocondesktop.composeapp.generated.resources.timeline_source_app_start
import flocondesktop.composeapp.generated.resources.timeline_source_crash
import flocondesktop.composeapp.generated.resources.timeline_source_database
import flocondesktop.composeapp.generated.resources.timeline_source_deeplink
import flocondesktop.composeapp.generated.resources.timeline_source_network
import flocondesktop.composeapp.generated.resources.timeline_source_table
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.view.components.NetworkImageBadge
import io.github.openflocon.flocondesktop.features.timeline.mapper.DurationLevel
import io.github.openflocon.flocondesktop.features.timeline.mapper.copyText
import io.github.openflocon.flocondesktop.features.timeline.mapper.durationLevel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.copyToClipboard
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun TimelineSource.label(): StringResource = when (this) {
    TimelineSource.Network -> Res.string.timeline_source_network
    TimelineSource.Database -> Res.string.timeline_source_database
    TimelineSource.Analytics -> Res.string.timeline_source_analytics
    TimelineSource.Accessibility -> Res.string.timeline_source_accessibility
    TimelineSource.Crash -> Res.string.timeline_source_crash
    TimelineSource.Table -> Res.string.timeline_source_table
    TimelineSource.Deeplink -> Res.string.timeline_source_deeplink
    TimelineSource.Adb -> Res.string.timeline_source_adb
    TimelineSource.AppStart -> Res.string.timeline_source_app_start
}

// the data of these sources are technical: they are displayed in mono
private val monoTitles = setOf(TimelineSource.Database, TimelineSource.Adb, TimelineSource.Deeplink)
private val monoSubtitles = setOf(TimelineSource.Crash, TimelineSource.Table, TimelineSource.Adb)

// the subtitle of these sources is a short name: it is a chip at the end of the line, so the row keeps one line
private val chipSubtitles = setOf(TimelineSource.Database, TimelineSource.Analytics, TimelineSource.Deeplink, TimelineSource.Accessibility)

private const val HoverAlpha = 0.3f
private const val SelectedBorderAlpha = 0.7f
private const val HighlightAlpha = 0.35f
private const val ErrorBoxAlpha = 0.12f

/**
 * One event: time and delta | category | content | meta. Only the content and the meta are highlighted.
 * [delta] is the time since the previous event, [highlight] the text of the filter, marked in the content.
 */
@Composable
internal fun TimelineRow(
    row: TimelineRowUiModel,
    delta: String?,
    selected: Boolean,
    hovered: Boolean,
    onClick: (TimelineRowUiModel) -> Unit,
    onHoverChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    highlight: String = "",
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val shape = FloconTheme.shapes.medium
    val interactionSource = remember { MutableInteractionSource() }
    val rowHovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(rowHovered) { onHoverChange(rowHovered) }
    DisposableEffect(Unit) { onDispose { onHoverChange(false) } }
    val (color, soft) = row.source.categoryColors()
    val isCrash = row.source == TimelineSource.Crash

    // the whole row reacts to the pointer, but only the part after the category is highlighted
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { onClick(row) }
            .padding(horizontal = spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        TimeColumn(
            time = row.time,
            delta = delta,
            modifier = Modifier.width(FloconTheme.sizes.timeColumn),
        )
        Spacer(Modifier.width(railGutter()))
        Box(modifier = Modifier.width(FloconTheme.sizes.categoryColumn)) {
            EventBadge(source = row.source)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                // hovering is a hint, selecting is the accent with a border: the two never look alike
                .background(
                    when {
                        selected -> colors.accentSoft
                        hovered -> colors.bg300.copy(alpha = HoverAlpha)
                        else -> Color.Transparent
                    },
                    shape,
                )
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
                .then(if (selected) Modifier.border(1.dp, colors.accent.copy(alpha = SelectedBorderAlpha), shape) else Modifier)
                .padding(horizontal = spacing.compact, vertical = spacing.tiny),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            RowContent(
                row = row,
                highlight = highlight,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = FloconTheme.sizes.rowHeight),
            )
            RowMeta(row = row)
            CopyButton(row = row, visible = rowHovered)
        }
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

@Composable
private fun RowContent(
    row: TimelineRowUiModel,
    highlight: String,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val highlightColor = colors.accent.copy(alpha = HighlightAlpha)
    val isCrash = row.source == TimelineSource.Crash
    // the status and the provider are displayed on the right
    val subtitle = row.subtitle.takeIf { row.network == null && row.source !in chipSubtitles }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        val network = row.network
        if (network != null) {
            NetworkTitle(network, row.phase, highlight = highlight, highlightColor = highlightColor)
        } else {
            val base = FloconTheme.typography.bodyMedium
            val title = if (row.source == TimelineSource.AppStart) stringResource(Res.string.timeline_app_started) else row.title
            val text = remember(title, highlight, colors, row.source) {
                val plain = if (row.source == TimelineSource.Database) {
                    sqlTitle(title, keywordColor = colors.ink, textColor = colors.inkMuted)
                } else {
                    AnnotatedString(title)
                }
                highlightMatches(plain, highlight, highlightColor)
            }
            Text(
                text = text,
                style = if (row.source in monoTitles) base.copy(fontFamily = FontFamily.Monospace) else base,
                fontWeight = FontWeight.Medium,
                color = if (isCrash) colors.catCrash else colors.ink,
                maxLines = 1,
                // the start of a link and its end both matter, the end of a query does not
                overflow = if (row.source == TimelineSource.Deeplink) TextOverflow.MiddleEllipsis else TextOverflow.Ellipsis,
            )
        }
        if (subtitle != null) {
            val base = FloconTheme.typography.labelMedium
            Text(
                text = remember(subtitle, highlight, highlightColor) { highlightMatches(AnnotatedString(subtitle), highlight, highlightColor) },
                style = if (row.source in monoSubtitles) base.copy(fontFamily = FontFamily.Monospace) else base,
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NetworkTitle(
    network: TimelineNetworkUiModel,
    phase: TimelinePhase,
    highlight: String,
    highlightColor: Color,
) {
    val colors = FloconTheme.colors
    // the response is the quieter of the two lines, unless the call failed: an error is never missed
    val error = network.statusCode?.let { it >= HTTP_ERROR } == true
    val isResponse = phase == TimelinePhase.Response && !error
    val title: AnnotatedString = remember(network, phase, highlight, colors) {
        val styled = buildAnnotatedString {
            if (network.host != null) {
                withStyle(SpanStyle(color = colors.inkMuted, fontWeight = FontWeight.Normal)) { append(network.host) }
                append(" ")
            }
            val pathStyle = if (isResponse) {
                SpanStyle(color = colors.inkMuted, fontWeight = FontWeight.Normal)
            } else {
                SpanStyle(color = colors.ink, fontWeight = FontWeight.Medium)
            }
            withStyle(pathStyle) { append(network.path) }
        }
        highlightMatches(styled, highlight, highlightColor)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        PhaseArrow(phase = phase)
        if (network.method.isNotEmpty()) {
            MethodPill(method = network.method)
        }
        if (network.isImage) {
            NetworkImageBadge()
        }
        Text(
            text = title,
            style = FloconTheme.typography.bodyMedium,
            maxLines = 1,
            // the host and the end of the path are the parts that tell the calls apart
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier
                .weight(1f, fill = false)
                // a failed call is boxed in red, whatever the family of its status
                .then(
                    if (error) {
                        Modifier
                            .background(colors.statusError.copy(alpha = ErrorBoxAlpha), FloconTheme.shapes.small)
                            .border(1.dp, colors.statusError, FloconTheme.shapes.small)
                            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny)
                    } else {
                        Modifier
                    }
                ),
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
            // the request has nothing to tell yet: the status and the duration are the ones of the response
            if (row.phase == TimelinePhase.Request) return@Row
            network.duration?.let {
                val level = durationLevel(network.durationMs)
                Text(
                    text = it,
                    style = monoStyle(small = true),
                    fontWeight = if (level == DurationLevel.Normal) null else FontWeight.SemiBold,
                    color = when (level) {
                        DurationLevel.Normal -> FloconTheme.colors.inkFaint
                        DurationLevel.Slow -> FloconTheme.colors.statusWarning
                        DurationLevel.VerySlow -> FloconTheme.colors.statusError
                    },
                    maxLines = 1,
                )
            }
            if (network.statusCode != null) {
                StatusPill(code = network.statusCode)
            } else {
                // no http code (ex: gRPC): what the app reports, if anything
                row.subtitle?.let { TagPill(text = it) }
            }
        } else if (row.source in chipSubtitles) {
            row.subtitle?.let { TagPill(text = it) }
        }
    }
}

// the space is always taken, so the content does not move when the pointer comes in
@Composable
private fun CopyButton(
    row: TimelineRowUiModel,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val text = row.copyText() ?: return
    Box(
        modifier = modifier
            .size(CopyButtonSize)
            .then(if (visible) Modifier.clickable { copyToClipboard(text) } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (visible) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = stringResource(Res.string.timeline_copy),
                tint = FloconTheme.colors.inkMuted,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

private val CopyButtonSize = 20.dp
private const val HTTP_ERROR = 400
