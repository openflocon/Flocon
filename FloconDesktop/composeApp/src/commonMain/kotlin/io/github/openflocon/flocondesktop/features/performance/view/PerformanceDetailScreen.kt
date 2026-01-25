package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.features.performance.MetricEventUiModel
import io.github.openflocon.flocondesktop.features.performance.PerformanceDetailViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDetailScreen(
    initialEvent: MetricEventUiModel,
) {
    val viewModel = koinViewModel<PerformanceDetailViewModel> { parametersOf(initialEvent) }
    val event by viewModel.event.collectAsStateWithLifecycle()
    val hasNext by viewModel.hasNext.collectAsStateWithLifecycle()
    val hasPrevious by viewModel.hasPrevious.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Detail - ${event.timestamp}") },
                actions = {
                    IconButton(
                        onClick = viewModel::onPrevious,
                        enabled = hasPrevious
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                    }
                    IconButton(
                        onClick = viewModel::onNext,
                        enabled = hasNext
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                },
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
                    event.ramMb?.let { MetricText(label = "RAM Usage", value = "$it MB") }
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
                    contentScale = ContentScale.Fit,
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

@Preview
@Composable
private fun PerformanceDetailScreenPreview() {
    FloconTheme {
        PerformanceDetailScreen(
            initialEvent = MetricEventUiModel(
                timestamp = "10:55:38.123",
                ramMb = "150",
                fps = "60.0",
                jankPercentage = "0.0%",
                battery = "85%",
                screenshotPath = null,
                rawFps = 60.0,
                rawRamMb = 150,
                isFpsDrop = false,
                rawJankPercentage = 0.0
            )
        )
    }
}
