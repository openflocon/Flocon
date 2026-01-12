package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.features.performance.MetricEvent
import io.github.openflocon.library.designsystem.FloconTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDetailScreen(
    event: MetricEvent,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Detail - ${event.timestamp}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FloconTheme.colorPalette.surface,
                    titleContentColor = FloconTheme.colorPalette.onSurface
                )
            )
        },
        containerColor = FloconTheme.colorPalette.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FloconTheme.colorPalette.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricText(label = "RAM Usage", value = "${event.ramMb} MB")
                    MetricText(label = "FPS", value = event.fps)
                    MetricText(label = "Jank Percentage", value = event.jankPercentage)
                    MetricText(label = "Battery", value = event.battery)
                }
            }

            // Screenshot
            if (event.screenshotPath != null) {
                AsyncImage(
                    model = "file://${event.screenshotPath}",
                    contentDescription = "Large Screenshot",
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f, fill = false),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    "No screenshot available",
                    style = FloconTheme.typography.bodyLarge,
                    color = FloconTheme.colorPalette.onSurface
                )
            }
        }
    }
}

@Composable
private fun MetricText(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.titleMedium,
            color = FloconTheme.colorPalette.onSurface
        )
        Text(
            text = value,
            style = FloconTheme.typography.bodyLarge,
            color = FloconTheme.colorPalette.onSurface
        )
    }
}
