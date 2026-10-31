package io.github.openflocon.library.designsystem.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import kotlin.math.abs
import kotlin.math.roundToInt

private val ThumbSize = 16.dp
private val TrackHeight = 4.dp

/**
 * The width kept on each side of the track for the thumbs: what a chart above or below the slider
 * ([FloconHistogram]) has to keep too, to be aligned with the positions of the slider.
 */
val FloconRangeSliderInset = ThumbSize / 2

/**
 * Two thumbs on the positions 0..[max]: the range goes from [start] to [end]. A position is a step, not a value,
 * the caller knows what it stands for.
 */
@Composable
fun FloconRangeSlider(
    start: Int,
    end: Int,
    max: Int,
    onRangeChange: (start: Int, end: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val density = LocalDensity.current
    var widthPx by remember { mutableStateOf(0) }
    val insetPx = with(density) { FloconRangeSliderInset.toPx() }
    val trackPx = (widthPx - insetPx * 2).coerceAtLeast(1f)
    val currentStart by rememberUpdatedState(start)
    val currentEnd by rememberUpdatedState(end)
    val currentOnChange by rememberUpdatedState(onRangeChange)
    var dragging by remember { mutableStateOf<Boolean?>(null) } // true: the start thumb

    fun positionAt(x: Float): Int = (((x - insetPx) / trackPx) * max).roundToInt().coerceIn(0, max)
    fun xOf(position: Int): Float = insetPx + trackPx * position / max.coerceAtLeast(1)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ThumbSize)
            .onSizeChanged { widthPx = it.width }
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(max) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // the closest thumb moves, the end one when both are on the same position and the click is after it
                        val toStart = abs(offset.x - xOf(currentStart))
                        val toEnd = abs(offset.x - xOf(currentEnd))
                        dragging = toStart < toEnd || (toStart == toEnd && offset.x < xOf(currentStart))
                    },
                    onDragEnd = { dragging = null },
                    onDragCancel = { dragging = null },
                ) { change, _ ->
                    val position = positionAt(change.position.x)
                    when (dragging) {
                        true -> currentOnChange(position.coerceAtMost(currentEnd), currentEnd)
                        false -> currentOnChange(currentStart, position.coerceAtLeast(currentStart))
                        null -> Unit
                    }
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .padding(horizontal = FloconRangeSliderInset)
                .fillMaxWidth()
                .height(TrackHeight)
                .background(colors.bg300, RoundedCornerShape(50)),
        )
        val startDp = with(density) { xOf(start).toDp() }
        val endDp = with(density) { xOf(end).toDp() }
        Box(
            Modifier
                .offset(x = startDp)
                .width((endDp - startDp).coerceAtLeast(0.dp))
                .height(TrackHeight)
                .background(colors.accent),
        )
        Thumb(offsetPx = xOf(start) - with(density) { ThumbSize.toPx() } / 2)
        Thumb(offsetPx = xOf(end) - with(density) { ThumbSize.toPx() } / 2)
    }
}

@Composable
private fun Thumb(offsetPx: Float) {
    Box(
        Modifier
            .offset { IntOffset(offsetPx.roundToInt(), 0) }
            .size(ThumbSize)
            .background(FloconTheme.colors.accent, CircleShape),
    )
}

@Preview
@Composable
private fun FloconRangeSliderPreview() {
    FloconTheme {
        var start by remember { mutableStateOf(2) }
        var end by remember { mutableStateOf(7) }
        FloconRangeSlider(
            start = start,
            end = end,
            max = 10,
            onRangeChange = { s, e ->
                start = s
                end = e
            },
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
        )
    }
}
