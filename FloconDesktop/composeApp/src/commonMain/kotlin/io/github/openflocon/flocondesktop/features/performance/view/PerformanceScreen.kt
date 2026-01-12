@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.features.performance.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.performance.PerformanceViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PerformanceScreen() {
    val viewModel = koinViewModel<PerformanceViewModel>()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()
    val packageName by viewModel.packageName.collectAsStateWithLifecycle()
    val intervalMs by viewModel.intervalMs.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val isMonitoring by viewModel.isMonitoring.collectAsStateWithLifecycle()

    FloconScaffold { paddingValues ->
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
                            modifier = Modifier.fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
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

            Box(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary),
                contentAlignment = Alignment.Center
            ) {
                val listState = rememberLazyListState()
                val scrollAdapter = rememberFloconScrollbarAdapter(listState)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(metrics) { event ->
                        MetricItemView(
                            event = event,
                            onClick = viewModel::onEventClicked
                        )
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier.fillMaxHeight()
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}
