package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * The vertical center of an item of the list, from the top of the list.
 * The offset of an item is measured from the start of the list, without the padding before the content: [startPadding]
 * (the bottom padding when the list is [reversed]). It is measured from the bottom when the list is [reversed].
 */
internal fun itemCenter(
    offset: Int,
    size: Int,
    viewportHeight: Float,
    reversed: Boolean,
    startPadding: Int = 0,
): Float {
    val center = offset + startPadding + size / 2f
    return if (reversed) viewportHeight - center else center
}

/**
 * The vertical bar linking some events, from [top] to [bottom] in pixels of the list.
 * A dot is drawn on each of [dots]: an end scrolled out of the list has no dot, the bar only leaves the list.
 */
internal data class CallRailSpan(
    val top: Float,
    val bottom: Float,
    val dots: List<Float>,
)

/**
 * [centers] are the vertical centers of the linked events displayed in the list. [leavesTop] and [leavesBottom] tell that
 * some linked events are scrolled out above or below, where the bar goes.
 * Null when there is nothing to draw: no linked event is displayed and the bar does not cross the list.
 */
internal fun callRailSpan(
    centers: List<Float>,
    leavesTop: Boolean,
    leavesBottom: Boolean,
    viewportHeight: Float,
): CallRailSpan? {
    if (centers.isEmpty()) {
        return if (leavesTop && leavesBottom) CallRailSpan(top = 0f, bottom = viewportHeight, dots = emptyList()) else null
    }
    return CallRailSpan(
        top = if (leavesTop) 0f else centers.min(),
        bottom = if (leavesBottom) viewportHeight else centers.max(),
        dots = centers,
    )
}

/**
 * The free space between the time and the category of a row, where the bar of a hovered call goes.
 * Shared by the rows and the app start banners, so the bar is at the same place in every item.
 */
@Composable
internal fun railGutter(): Dp = FloconTheme.spacing.medium

/**
 * Drawn over the list: the bar and the dots of the hovered events, going through the events between them.
 * [linkedIndices] are the indices in the list of the events linked to the hovered one: the request and the response of a call,
 * or the same event repeated. [leavesAfterLoaded] tells that some are not loaded yet, further than the last loaded event.
 */
@Composable
internal fun CallRail(
    listState: LazyListState,
    linkedIndices: List<Int>,
    leavesAfterLoaded: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val sizes = FloconTheme.sizes
    val gutter = railGutter()
    val halo = colors.bg100

    Canvas(modifier = modifier) {
        if (linkedIndices.isEmpty()) return@Canvas
        val layoutInfo = listState.layoutInfo
        val items = layoutInfo.visibleItemsInfo
        val first = items.firstOrNull()?.index ?: return@Canvas
        val last = items.last().index
        val reversed = layoutInfo.reverseLayout
        val centers = linkedIndices.mapNotNull { index ->
            items.firstOrNull { it.index == index }?.let {
                itemCenter(
                    offset = it.offset,
                    size = it.size,
                    viewportHeight = size.height,
                    reversed = reversed,
                    startPadding = layoutInfo.beforeContentPadding,
                )
            }
        }
        // the first items of the list are at the top, or at the bottom when it is reversed
        val beforeStart = linkedIndices.any { it < first }
        val afterEnd = linkedIndices.any { it > last } || leavesAfterLoaded
        val span = callRailSpan(
            centers = centers,
            leavesTop = if (reversed) afterEnd else beforeStart,
            leavesBottom = if (reversed) beforeStart else afterEnd,
            viewportHeight = size.height,
        ) ?: return@Canvas
        // the center of the gutter between the time and the category of the rows
        val x = (spacing.compact + sizes.timeColumn + spacing.small + gutter / 2).toPx()

        drawLine(
            color = color,
            start = Offset(x, span.top),
            end = Offset(x, span.bottom),
            strokeWidth = 2.dp.toPx(),
        )
        span.dots.forEach { y ->
            drawCircle(color = halo, radius = 6.dp.toPx(), center = Offset(x, y))
            drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(x, y))
        }
    }
}
