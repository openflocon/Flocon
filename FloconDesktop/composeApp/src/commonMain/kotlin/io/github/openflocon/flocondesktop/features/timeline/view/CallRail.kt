package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

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
 * Null when there is nothing to draw: none of the two lines is displayed.
 */
internal fun callRailSpan(
    upperCenter: Float?,
    lowerCenter: Float?,
    viewportHeight: Float,
): CallRailSpan? = when {
    upperCenter != null && lowerCenter != null -> CallRailSpan(upperCenter, lowerCenter, topDot = true, bottomDot = true)
    // the request is scrolled out below the list: the bar leaves the list on that side
    upperCenter != null -> CallRailSpan(upperCenter, viewportHeight, topDot = true, bottomDot = false)
    // the response is scrolled out above the list
    lowerCenter != null -> CallRailSpan(0f, lowerCenter, topDot = false, bottomDot = true)
    else -> null
}

/**
 * The keys of the displayed items strictly between the two lines of a call, [keys] being the keys of the displayed items from the top.
 * An end scrolled out of the list is the edge of the list. Empty when the call has no response: [upperKey] is null.
 */
internal fun keysBetween(
    keys: List<String>,
    upperKey: String?,
    lowerKey: String?,
): Set<String> {
    if (upperKey == null || lowerKey == null) return emptySet()
    val upper = keys.indexOf(upperKey)
    val lower = keys.indexOf(lowerKey)
    if (upper < 0 && lower < 0) return emptySet()
    val from = if (upper < 0) 0 else upper + 1
    val to = if (lower < 0) keys.size else lower
    return if (from < to) keys.subList(from, to).toSet() else emptySet()
}

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
    val color = colors.catNetwork
    val halo = colors.bg100

    Canvas(modifier = modifier) {
        if (upperKey == null || lowerKey == null) return@Canvas
        val items = listState.layoutInfo.visibleItemsInfo
        val span = callRailSpan(
            upperCenter = items.centerOf(upperKey),
            lowerCenter = items.centerOf(lowerKey),
            viewportHeight = size.height,
        ) ?: return@Canvas
        // the center of the marker column of the rows
        val x = (spacing.compact + sizes.timeColumn + spacing.small + sizes.markerColumn / 2).toPx()

        drawLine(
            color = color,
            start = Offset(x, span.top),
            end = Offset(x, span.bottom),
            strokeWidth = 2.dp.toPx(),
        )
        listOf(span.top to span.topDot, span.bottom to span.bottomDot).forEach { (y, visible) ->
            if (visible) {
                drawCircle(color = halo, radius = 8.dp.toPx(), center = Offset(x, y))
                drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}

private fun List<LazyListItemInfo>.centerOf(key: String): Float? = firstOrNull { it.key == key }?.let { it.offset + it.size / 2f }
