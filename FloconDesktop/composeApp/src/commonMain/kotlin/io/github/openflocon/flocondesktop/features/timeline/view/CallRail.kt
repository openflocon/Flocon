package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * The vertical center of an item of the list, from the top of the list.
 * The offset of an item is measured from the start of the list: its bottom when the list is [reversed].
 */
internal fun itemCenter(
    offset: Int,
    size: Int,
    viewportHeight: Float,
    reversed: Boolean,
): Float {
    val center = offset + size / 2f
    return if (reversed) viewportHeight - center else center
}

/**
 * The vertical bar linking the request and the response of a call, from [top] to [bottom] in pixels of the list.
 * A dot is drawn on each end that is displayed: an end scrolled out of the list is only a bar leaving the list.
 */
internal data class CallRailSpan(
    val top: Float,
    val bottom: Float,
    val topDot: Boolean,
    val bottomDot: Boolean,
)

/**
 * [upperCenter] and [lowerCenter] are the vertical centers of the two lines of the call in the list, null when the line is
 * not displayed. The response is the upper one: the list is sorted from the newest.
 * A request scrolled out below the list makes the bar leave the list at the bottom, a response scrolled out above at the top.
 * Null when there is nothing to draw: none of the two lines is displayed.
 */
internal fun callRailSpan(
    upperCenter: Float?,
    lowerCenter: Float?,
    viewportHeight: Float,
): CallRailSpan? = when {
    upperCenter != null && lowerCenter != null -> CallRailSpan(upperCenter, lowerCenter, topDot = true, bottomDot = true)
    upperCenter != null -> CallRailSpan(upperCenter, viewportHeight, topDot = true, bottomDot = false)
    lowerCenter != null -> CallRailSpan(0f, lowerCenter, topDot = false, bottomDot = true)
    else -> null
}

/**
 * The free space between the time and the category of a row, where the bar of a hovered call goes.
 * Shared by the rows and the app start banners, so the bar is at the same place in every item.
 */
@Composable
internal fun railGutter(): Dp = FloconTheme.spacing.medium

/**
 * Drawn over the list: the bar and the two dots of the hovered call, going through the events between its request and its response.
 * [upperKey] is the key of the response, [lowerKey] the one of the request.
 */
@Composable
internal fun CallRail(
    listState: LazyListState,
    upperKey: String?,
    lowerKey: String?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val sizes = FloconTheme.sizes
    val gutter = railGutter()
    val color = colors.catNetwork
    val halo = colors.bg100

    Canvas(modifier = modifier) {
        if (upperKey == null || lowerKey == null) return@Canvas
        val layoutInfo = listState.layoutInfo
        val items = layoutInfo.visibleItemsInfo
        val reversed = layoutInfo.reverseLayout
        val span = callRailSpan(
            upperCenter = items.centerOf(upperKey, size.height, reversed),
            lowerCenter = items.centerOf(lowerKey, size.height, reversed),
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
        listOf(span.top to span.topDot, span.bottom to span.bottomDot).forEach { (y, visible) ->
            if (visible) {
                drawCircle(color = halo, radius = 6.dp.toPx(), center = Offset(x, y))
                drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}

private fun List<LazyListItemInfo>.centerOf(key: String, viewportHeight: Float, reversed: Boolean): Float? = firstOrNull { it.key == key }
    ?.let { itemCenter(offset = it.offset, size = it.size, viewportHeight = viewportHeight, reversed = reversed) }
