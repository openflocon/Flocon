package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.performance.MetricEventUiModel
import io.github.openflocon.flocondesktop.features.performance.previewMetricsEvent
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

private val imageSize = 40.dp

@Composable
internal fun MetricsHeaderView(
    modifier: Modifier = Modifier,
) {
    val bodySmall = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp)

    FloconSurface(
        modifier = modifier,
        shape = FloconTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = FloconTheme.colorPalette.primary,
        contentColor = FloconTheme.colorPalette.onPrimary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(imageSize).background(Color.Red),
            )
            Text(
                modifier = Modifier.width(140.dp),
                text = "Time",
                style = bodySmall,
                color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.5f)
            )

            Text("FPS", style = bodySmall, modifier = Modifier.width(140.dp))

            Text(
                "Jank",
                style = bodySmall,
                modifier = Modifier.width(140.dp)
            )

            Text("RAM", style = bodySmall, modifier = Modifier.width(140.dp))

            Text("Battery", style = bodySmall, modifier = Modifier.width(140.dp))
        }
    }
}

@Preview
@Composable
private fun MetricItemViewPreview() {
    FloconTheme {
        MetricItemView(
            event = previewMetricsEvent(),
            onClick = {}
        )
    }
}
