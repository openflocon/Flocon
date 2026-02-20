package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.performance.model.MetricsAverageUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun PerformanceAverageView(
    average: MetricsAverageUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AverageMetricBox(
            modifier = Modifier.weight(1f),
            title = "Avg. FPS",
            value = average.fps ?: "-"
        )
        AverageMetricBox(
            modifier = Modifier.weight(1f),
            title = "Avg. RAM",
            value = average.ram ?: ""
        )
        AverageMetricBox(
            modifier = Modifier.weight(1f),
            title = "Avg. Jank",
            value = average.jank ?: ""
        )
    }
}

@Composable
private fun AverageMetricBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        Text(
            text = title,
            style = FloconTheme.typography.titleSmall,
            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = FloconTheme.typography.titleMedium,
        )
    }
}