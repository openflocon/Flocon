package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineActivityUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlin.math.max
import kotlin.math.min

/**
 * The activity at a glance: one row per displayed category, a tick where events happened, and a dashed
 * line where an app starts. It shows the bursts, and where the crash is.
 */
@Composable
internal fun TimelineActivityStrip(
    activity: TimelineActivityUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium
    val rowColors = activity.rows.map { it.source.categoryColors().first }
    val sessionColor = colors.accent

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.bg200, shape)
            .border(1.dp, colors.line, shape)
            .padding(FloconTheme.spacing.small),
    ) {
        if (activity.rows.isEmpty()) return@Canvas

        val gap = 2.dp.toPx()
        val rowHeight = (size.height - gap * (activity.rows.size - 1)) / activity.rows.size
        val bucketWidth = size.width / activity.bucketCount
        // a slice is thinner than a pixel on a small strip: keep the ticks visible
        val tickWidth = max(bucketWidth, 1.5.dp.toPx())

        activity.rows.forEachIndexed { index, row ->
            val y = index * (rowHeight + gap)
            row.buckets.forEach { (bucket, count) ->
                drawRect(
                    color = rowColors[index].copy(alpha = min(1f, 0.55f + 0.15f * (count - 1))),
                    topLeft = Offset(bucket * bucketWidth, y),
                    size = Size(tickWidth, rowHeight),
                )
            }
        }

        val dashes = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
        activity.sessionBuckets.forEach { bucket ->
            val x = (bucket + 0.5f) * bucketWidth
            drawLine(
                color = sessionColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashes,
            )
        }
    }
}
