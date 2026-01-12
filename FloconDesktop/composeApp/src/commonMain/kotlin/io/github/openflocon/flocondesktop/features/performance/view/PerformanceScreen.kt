@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.openflocon.flocondesktop.features.performance.MetricEvent
import io.github.openflocon.flocondesktop.features.performance.PerformanceViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.*
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@Composable
fun PerformanceScreen() {
    val viewModel = koinViewModel<PerformanceViewModel>()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()
    val packageName by viewModel.packageName.collectAsStateWithLifecycle()
    val intervalMs by viewModel.intervalMs.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val isMonitoring by viewModel.isMonitoring.collectAsStateWithLifecycle()

    FloconScaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device Selection
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            text = selectedDevice ?: "Select Device",
                            //onValueChange = {},
                            //readOnly = true,
                            //label = "Device Serial",
                            //trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = FloconTheme.colorPalette.surface
                        ) {
                            devices.forEach { deviceSerial ->
                                DropdownMenuItem(
                                    text = { Text(deviceSerial) },
                                    onClick = {
                                        viewModel.onDeviceSelected(deviceSerial)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Package Name
                FloconTextField(
                    modifier = Modifier.weight(1f),
                    value = packageName,
                    onValueChange = viewModel::onPackageNameChanged,
                    placeholder = { Text("Package Name (e.g. com.example.app)") },
                    label = { Text("Filter Package") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interval Control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Fetch every:", style = FloconTheme.typography.bodyMedium)
                    FloconIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = viewModel::decrementInterval,
                        tooltip = "Decrease interval"
                    )
                    FloconTextField(
                        modifier = Modifier.width(100.dp),
                        value = intervalMs.toString(),
                        onValueChange = { /* Read only or handle numeric input */ },
                        trailingComponent = { Text("ms") }
                    )
                    FloconIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = viewModel::incrementInterval,
                        tooltip = "Increase interval"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                FloconButton(
                    onClick = viewModel::toggleMonitoring,
                    // TODO colors = if (isMonitoring) {
                    // TODO     ButtonDefaults.buttonColors(containerColor = FloconTheme.colorPalette.error)
                    // TODO } else {
                    // TODO     ButtonDefaults.buttonColors()
                    // TODO }
                ) {
                    Text(
                        text = if (isMonitoring) "Stop Monitoring" else "Start Monitoring",
                    )
                }
            }

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(metrics) { event ->
                    MetricItem(
                        event = event,
                        onClick = viewModel::onEventClicked
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(
    event: MetricEvent,
    onClick: (MetricEvent) -> Unit,
) {
    FloconSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = FloconTheme.shapes.medium,
        tonalElevation = 2.dp,
        onClick = { onClick(event) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.timestamp,
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("RAM: ${event.ramMb} MB", style = FloconTheme.typography.bodyMedium)
                    Text("FPS: ${event.fps}", style = FloconTheme.typography.bodyMedium)
                    Text("Jank: ${event.jankPercentage}", style = FloconTheme.typography.bodyMedium)
                    Text("Battery: ${event.battery}", style = FloconTheme.typography.bodyMedium)
                }
            }

            if (event.screenshotPath != null) {
                AsyncImage(
                    model = File(event.screenshotPath),
                    contentDescription = "Screenshot at ${event.timestamp}",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloconCircularProgressIndicator()
                }
            }
        }
    }
}
