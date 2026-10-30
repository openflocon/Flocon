package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val ControlRadius = 8.dp
private val SegmentRadius = 6.dp
private val SegmentIconSize = 15.dp
private val SegmentPadding = 2.dp

@Immutable
data class FloconSegmentedIconOption(
    val icon: ImageVector,
    val tooltip: String,
)

/** Segmented icon buttons (e.g. [list | grid]), the selected background slides like a switch. */
@Composable
fun FloconSegmentedIconToggle(
    options: ImmutableList<FloconSegmentedIconOption>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val segmentWidth = FloconTheme.sizes.controlHeight
    val indicatorOffset by animateDpAsState(
        targetValue = segmentWidth * selectedIndex,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
    )
    Box(
        modifier = modifier
            .height(FloconTheme.sizes.pageBarControlHeight)
            .clip(RoundedCornerShape(ControlRadius))
            .background(FloconTheme.colors.bg200)
            .border(1.dp, FloconTheme.colors.line, RoundedCornerShape(ControlRadius))
            .padding(SegmentPadding),
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .fillMaxHeight()
                .width(segmentWidth)
                .clip(RoundedCornerShape(SegmentRadius))
                .background(FloconTheme.colors.bg300),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            options.forEachIndexed { index, option ->
                Segment(
                    option = option,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Segment(
    option: FloconSegmentedIconOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    WithTooltip(option.tooltip) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(FloconTheme.sizes.controlHeight)
                .clip(RoundedCornerShape(SegmentRadius))
                .clickable(onClick = onClick)
                .pointerHoverIcon(PointerIcon.Hand),
            contentAlignment = Alignment.Center,
        ) {
            val tint by animateColorAsState(if (selected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted)
            FloconIcon(
                imageVector = option.icon,
                tint = tint,
                modifier = Modifier.size(SegmentIconSize),
            )
        }
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
