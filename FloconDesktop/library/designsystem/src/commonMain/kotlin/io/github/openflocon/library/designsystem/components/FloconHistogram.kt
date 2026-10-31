package io.github.openflocon.library.designsystem.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val BarsHeight = 40.dp
private const val OutsideRangeAlpha = 0.5f

/**
 * One bar per bucket, as high as its value. The buckets from [start] (included) to [end] (excluded), the positions
 * of a [FloconRangeSlider] of the same size, are the ones in the range: the other bars are drawn at half the opacity.
 * The bars keep the height of the highest bucket of all, so they don't grow when the range gets smaller.
 */
@Composable
fun FloconHistogram(
    values: List<Int>,
    start: Int,
    end: Int,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val highest = (values.maxOrNull() ?: 0).coerceAtLeast(1)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BarsHeight)
            // like the slider, to be aligned with its positions
            .padding(horizontal = FloconRangeSliderInset),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        values.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                if (value > 0) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(value.toFloat() / highest)
                            .background(colors.accent.copy(alpha = if (index in start until end) 1f else OutsideRangeAlpha)),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FloconHistogramPreview() {
    FloconTheme {
        FloconHistogram(
            values = listOf(0, 1, 4, 8, 6, 2, 3, 1, 0, 0),
            start = 2,
            end = 7,
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
        )
    }
}
