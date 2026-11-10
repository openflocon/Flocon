package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val ControlRadius = 8.dp
private val SegmentRadius = 6.dp
private val SegmentIconSize = 15.dp
private val SegmentPadding = 2.dp
private const val ShortTextLength = 2
private const val LongTextWidthFactor = 1.5f

@Immutable
data class FloconSegmentedIconOption(
    val icon: ImageVector,
    val tooltip: String,
)

/** Segmented icon buttons (e.g. [list | grid]), the selected background slides like a switch. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloconSegmentedIconToggle(
    options: ImmutableList<FloconSegmentedIconOption>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SlidingSegmentedControl(
        segmentWidths = options.map { FloconTheme.sizes.controlHeight },
        selectedIndex = selectedIndex,
        modifier = modifier,
    ) { index, selected, _ ->
        val option = options[index]
        WithTooltip(option.tooltip) {
            SegmentBox(width = FloconTheme.sizes.controlHeight, onClick = { onSelect(index) }) {
                val tint by animateColorAsState(if (selected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted)
                FloconIcon(
                    imageVector = option.icon,
                    tint = tint,
                    modifier = Modifier.size(SegmentIconSize),
                )
            }
        }
    }
}

/** Same control as [FloconSegmentedIconToggle] with a text per segment (e.g. [Auto | 1 | 2 | 3]). */
@Composable
fun FloconSegmentedTextToggle(
    options: ImmutableList<String>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // a segment is as wide as a square one, unless its text is longer than a few characters
    val segmentWidths = options.map {
        if (it.length > ShortTextLength) FloconTheme.sizes.controlHeight * LongTextWidthFactor else FloconTheme.sizes.controlHeight
    }
    SlidingSegmentedControl(
        segmentWidths = segmentWidths,
        selectedIndex = selectedIndex,
        modifier = modifier,
    ) { index, selected, segmentWidth ->
        SegmentBox(width = segmentWidth, onClick = { onSelect(index) }) {
            val tint by animateColorAsState(if (selected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted)
            Text(
                text = options[index],
                color = tint,
                style = FloconTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SlidingSegmentedControl(
    segmentWidths: List<Dp>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    segment: @Composable (index: Int, selected: Boolean, width: Dp) -> Unit,
) {
    val selectedWidth = segmentWidths.getOrElse(selectedIndex) { 0.dp }
    val indicatorOffset by animateDpAsState(
        targetValue = segmentWidths.take(selectedIndex).fold(0.dp) { acc, width -> acc + width },
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
    )
    val indicatorWidth by animateDpAsState(
        targetValue = selectedWidth,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
    )
    Box(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(RoundedCornerShape(ControlRadius))
            .background(FloconTheme.colors.bg100)
            .border(1.dp, FloconTheme.colors.line, RoundedCornerShape(ControlRadius))
            .padding(SegmentPadding),
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .fillMaxHeight()
                .width(indicatorWidth)
                .clip(RoundedCornerShape(SegmentRadius))
                .background(FloconTheme.colors.bg300)
                .border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(SegmentRadius)),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            segmentWidths.forEachIndexed { index, width ->
                segment(index, index == selectedIndex, width)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SegmentBox(
    width: Dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    // no indication: the theme ripple is a yellow square
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .clip(RoundedCornerShape(SegmentRadius))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview
@Composable
private fun FloconSegmentedIconTogglePreview() {
    FloconTheme {
        FloconSegmentedIconToggle(
            options = persistentListOf(
                FloconSegmentedIconOption(icon = Icons.AutoMirrored.Outlined.List, tooltip = "List"),
                FloconSegmentedIconOption(icon = Icons.Outlined.GridView, tooltip = "Grid"),
            ),
            selectedIndex = 1,
            onSelect = {},
        )
    }
}
