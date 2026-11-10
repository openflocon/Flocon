package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material.icons.outlined.PhotoCamera
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownPanelAnchor
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_screenshots
import flocondesktop.composeapp.generated.resources.timeline_screenshots_failing
import flocondesktop.composeapp.generated.resources.timeline_screenshots_interval_default
import flocondesktop.composeapp.generated.resources.timeline_screenshots_interval_option
import flocondesktop.composeapp.generated.resources.timeline_screenshots_live
import flocondesktop.composeapp.generated.resources.timeline_screenshots_start
import flocondesktop.composeapp.generated.resources.timeline_screenshots_unavailable
import io.github.openflocon.domain.timeline.models.DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS
import io.github.openflocon.domain.timeline.models.TimelineScreenshotIntervalsSeconds
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotsStatus
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip
import org.jetbrains.compose.resources.stringResource

private val BorderWidth = 1.dp
private val IconSize = 16.dp
private val ChevronSize = 14.dp
private const val OutlineAlpha = 0.55f
private const val BadgeAlpha = 0.14f

/**
 * The screenshots of the app taken at a regular interval, shown next to the events: one pill with three looks.
 * - not available (the device cannot take them): grey, nothing to click, a tooltip tells why
 * - available and off: amber outline and a "Start" badge, a click turns them on
 * - available and on: amber fill, and a badge with the interval that opens the list of intervals
 * [status]: the last captures did not work, or the device cannot take them.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenshotsControl(
    enabled: Boolean,
    intervalSeconds: Int,
    status: TimelineScreenshotsStatus,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val unavailable = status == TimelineScreenshotsStatus.Unavailable
    val on = enabled && !unavailable
    val shape = FloconTheme.shapes.medium
    val borderColor = when {
        unavailable -> colors.lineStrong
        else -> colors.accent.copy(alpha = OutlineAlpha)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        WithTooltip(tooltip = if (unavailable) stringResource(Res.string.timeline_screenshots_unavailable) else null) {
            Row(
                modifier = Modifier
                    .height(FloconTheme.sizes.controlHeight)
                    .clip(shape)
                    .border(BorderWidth, borderColor, shape),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ScreenshotsHead(
                    on = on,
                    unavailable = unavailable,
                    onClick = { onAction(TimelineAction.SetScreenshots(!enabled)) },
                )
                when {
                    unavailable -> Unit

                    on -> ScreenshotsIntervalBadge(
                        intervalSeconds = intervalSeconds,
                        onSelect = { onAction(TimelineAction.SetScreenshotInterval(it)) },
                    )

                    else -> ScreenshotsStartBadge(onClick = { onAction(TimelineAction.SetScreenshots(true)) })
                }
            }
        }
        // a failing capture makes no sense next to a pill that is off, a device that cannot take them is said by the tooltip
        if (on && status == TimelineScreenshotsStatus.Failing) {
            WithTooltip(tooltip = stringResource(Res.string.timeline_screenshots_failing)) {
                CategoryDot(color = colors.statusWarning)
            }
        }
    }
}

/** The camera and the label: it turns the screenshots on and off, unless the device cannot take them. */
@Composable
private fun ScreenshotsHead(
    on: Boolean,
    unavailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val background = when {
        unavailable -> colors.bg200
        on -> if (hovered) colors.accent.copy(alpha = 0.88f) else colors.accent
        else -> if (hovered) colors.accentSoft else Color.Transparent
    }
    val contentColor = when {
        unavailable -> colors.inkFaint
        on -> colors.accentInk
        else -> colors.ink
    }
    val iconColor = if (on || unavailable) contentColor else colors.accent
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(background)
            .hoverable(interaction)
            .then(
                if (unavailable) {
                    Modifier
                } else {
                    Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(
                            role = Role.Switch,
                            interactionSource = interaction,
                            // the hover is the feedback
                            indication = null,
                            onClick = onClick,
                        )
                },
            )
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconIcon(
            imageVector = if (unavailable) Icons.Outlined.NoPhotography else Icons.Outlined.PhotoCamera,
            tint = iconColor,
            modifier = Modifier.size(IconSize),
        )
        Text(
            text = stringResource(Res.string.timeline_screenshots),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun ScreenshotsStartBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(if (hovered) colors.accent.copy(alpha = 0.88f) else colors.accent)
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                role = Role.Button,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.timeline_screenshots_start),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.accentInk,
            maxLines = 1,
        )
    }
}

/** "Live · 5 s": the screenshots are being taken, a click lists the intervals. */
@Composable
private fun ScreenshotsIntervalBadge(
    intervalSeconds: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    var expanded by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = { expanded = true },
        anchor = DropdownPanelAnchor.BottomStart,
        modifier = modifier.fillMaxHeight(),
        anchorContent = {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(colors.accent.copy(alpha = if (hovered || expanded) BadgeAlpha * 2 else BadgeAlpha))
                    .hoverable(interaction)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable(
                        role = Role.Button,
                        interactionSource = interaction,
                        indication = null,
                        onClick = { expanded = true },
                    )
                    .padding(start = FloconTheme.spacing.small, end = FloconTheme.spacing.compact),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            ) {
                CategoryDot(color = colors.accent)
                Text(
                    text = stringResource(Res.string.timeline_screenshots_live, intervalSeconds),
                    style = FloconTheme.typography.labelMedium,
                    color = colors.accent,
                    maxLines = 1,
                )
                FloconIcon(
                    imageVector = Icons.Outlined.ExpandMore,
                    tint = colors.accent,
                    modifier = Modifier.size(ChevronSize),
                )
            }
        },
    ) {
        TimelineScreenshotIntervalsSeconds.forEach { seconds ->
            FloconDropdownMenuItem(
                text = stringResource(Res.string.timeline_screenshots_interval_option, seconds),
                leadingIcon = if (seconds == intervalSeconds) Icons.Outlined.Check else null,
                secondaryAction = if (seconds == DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS) {
                    {
                        Text(
                            text = stringResource(Res.string.timeline_screenshots_interval_default),
                            style = FloconTheme.typography.labelSmall,
                            color = colors.inkFaint,
                        )
                    }
                } else {
                    null
                },
                onClick = {
                    expanded = false
                    onSelect(seconds)
                },
            )
        }
    }
}

@Composable
private fun ScreenshotsControlPreviewContent(enabled: Boolean, status: TimelineScreenshotsStatus) {
    FloconTheme {
        ScreenshotsControl(
            enabled = enabled,
            intervalSeconds = 5,
            status = status,
            onAction = {},
            modifier = Modifier.padding(FloconTheme.spacing.medium),
        )
    }
}

@Preview
@Composable
private fun ScreenshotsControlOnPreview() = ScreenshotsControlPreviewContent(enabled = true, status = TimelineScreenshotsStatus.Working)

@Preview
@Composable
private fun ScreenshotsControlOffPreview() = ScreenshotsControlPreviewContent(enabled = false, status = TimelineScreenshotsStatus.Working)

@Preview
@Composable
private fun ScreenshotsControlFailingPreview() = ScreenshotsControlPreviewContent(enabled = true, status = TimelineScreenshotsStatus.Failing)

@Preview
@Composable
private fun ScreenshotsControlUnavailablePreview() = ScreenshotsControlPreviewContent(enabled = false, status = TimelineScreenshotsStatus.Unavailable)
