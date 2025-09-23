package io.github.openflocon.flocondesktop.device.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.device.models.BatteryUiState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextValue

@Composable
internal fun BatteryPage(
    state: BatteryUiState
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
        FloconTextValue(label = "Technology", value = state.technology.orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(label = "Health", value = state.health?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Capacity",
            value = state.capacityLevel?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "AC Powered", value = state.acPowered?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Charge counter",
            value = state.chargeCounter?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Charging policy",
            value = state.chargingPolicy?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Charging state",
            value = state.chargingState?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Max Charging current",
            value = state.maxChargingCurrent?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Max Charging voltage",
            value = state.maxChargingVoltage?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Dock powered",
            value = state.dockPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Level", value = state.level?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(label = "Voltage", value = state.voltage?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "Temperature",
            value = state.temperature?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(
            label = "Wireless powered",
            value = state.wirelessPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Scale", value = state.scale?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
        FloconTextValue(
            label = "USB Powered",
            value = state.usbPowered?.toString().orEmpty(),
            valueContainerColor = FloconTheme.colorPalette.secondary
        )
        FloconTextValue(label = "Present", value = state.present?.toString().orEmpty(), valueContainerColor = FloconTheme.colorPalette.secondary)
    }
}
