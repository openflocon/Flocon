package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.common.ui.isInPreview
import io.github.openflocon.flocondesktop.features.performance.MetricEventUiModel
import io.github.openflocon.flocondesktop.features.performance.previewMetricsEvent
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

private val imageSize = 40.dp

@Composable
internal fun MetricItemView(
    event: MetricEventUiModel,
    onClick: (MetricEventUiModel) -> Unit,
) {
    val bodySmall = FloconTheme.typography.bodySmall.copy(fontSize = 11.sp)

    FloconSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = FloconTheme.shapes.medium,
        tonalElevation = 2.dp,
        onClick = { onClick(event) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(isInPreview) {
                Box(
                    modifier = Modifier.size(imageSize).background(Color.Red),
                )
            } else if (event.screenshotPath != null) {
                AsyncImage(
                    model = "file://${event.screenshotPath}",
                    contentDescription = "Screenshot at ${event.timestamp}",
                    modifier = Modifier
                        .size(imageSize)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                // empty placeholder
                Box(
                    modifier = Modifier.size(imageSize),
                )
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = event.timestamp,
                    style = bodySmall,
                    color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("FPS: ${event.fps}", style = bodySmall)
                    Text("Jank: ${event.jankPercentage}", style = bodySmall)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    event.ramMb?.let { Text("RAM: $it", style = bodySmall) }
                    Text("Battery: ${event.battery}", style = bodySmall)
                }
            }
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
