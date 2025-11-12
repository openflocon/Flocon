package io.github.openflocon.flocondesktop.device.pages.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun BatteryPage(
    deviceSerial: String
) {
    val viewModel = koinViewModel<BatteryViewModel> { parametersOf(deviceSerial) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun Content(
    uiState: BatteryUiState,
    onAction: (BatteryAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.secondary,
                shape = FloconTheme.shapes.medium
            )
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        FloconTextValue(label = "Technology", value = uiState.technology.orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(label = "Health", value = uiState.health?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Capacity",
            value = uiState.capacityLevel?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "AC Powered", value = uiState.acPowered?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Charge counter",
            value = uiState.chargeCounter?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Charging policy",
            value = uiState.chargingPolicy?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Charging state",
            value = uiState.chargingState?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Max Charging current",
            value = uiState.maxChargingCurrent?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Max Charging voltage",
            value = uiState.maxChargingVoltage?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Dock powered",
            value = uiState.dockPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Level", value = uiState.level?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(label = "Voltage", value = uiState.voltage?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Temperature",
            value = uiState.temperature?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Wireless powered",
            value = uiState.wirelessPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Scale", value = uiState.scale?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "USB Powered",
            value = uiState.usbPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Present", value = uiState.present?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
    }
}
