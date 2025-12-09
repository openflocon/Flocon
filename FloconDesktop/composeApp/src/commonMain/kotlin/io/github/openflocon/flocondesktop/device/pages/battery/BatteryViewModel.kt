package io.github.openflocon.flocondesktop.device.pages.battery

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BatteryViewModel(
    deviceSerial: String,
    sendCommandUseCase: SendCommandUseCase
) : PageViewModel(deviceSerial, sendCommandUseCase) {

    private val _uiState = MutableStateFlow(
        BatteryUiState(
            acPowered = null,
            usbPowered = null,
            wirelessPowered = null,
            dockPowered = null,
            maxChargingCurrent = null,
            maxChargingVoltage = null,
            chargeCounter = null,
            status = null,
            health = null,
            present = null,
            level = null,
            scale = null,
            voltage = null,
            temperature = null,
            technology = null,
            chargingState = null,
            chargingPolicy = null,
            capacityLevel = null
        )
    )
    val uiState: StateFlow<BatteryUiState> = _uiState.asStateFlow()

    init {
        refreshBattery()
    }

    fun onAction(action: BatteryAction) {

    }

    private fun refreshBattery() {
        viewModelScope.launch(Dispatchers.IO) {
            val batteryInfo = sendCommand("shell", "dumpsys", "battery")

            _uiState.update {
                it.copy(
                    acPowered = AC_POWERED_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toBoolean() ?: false,
                    usbPowered = USB_POWERED_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toBoolean() ?: false,
                    wirelessPowered = WIRELESS_POWERED_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toBoolean()
                        ?: false,
                    dockPowered = DOCK_POWERED_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toBoolean() ?: false,
                    maxChargingCurrent = MAX_CHARGING_CURRENT_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    maxChargingVoltage = MAX_CHARGING_VOLTAGE_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    chargeCounter = CHARGE_COUNTER_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    status = STATUS_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    health = HEALTH_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    present = PRESENT_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toBoolean() ?: false,
                    level = LEVEL_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    scale = SCALE_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    voltage = VOLTAGE_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    temperature = TEMPERATURE_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    technology = TECHNOLOGY_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1),
                    chargingState = CHARGING_STATE_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    chargingPolicy = CHARGING_POLICY_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull(),
                    capacityLevel = CAPACITY_LEVEL_REGEX.toRegex().find(batteryInfo)?.groupValues?.get(1)?.toIntOrNull()
                )
            }
        }
    }

    companion object {
        private const val AC_POWERED_REGEX = """AC powered:\s+(true|false)"""
        private const val USB_POWERED_REGEX = """USB powered:\s+(true|false)"""
        private const val WIRELESS_POWERED_REGEX = """Wireless powered:\s+(true|false)"""
        private const val DOCK_POWERED_REGEX = """Dock powered:\s+(true|false)"""
        private const val MAX_CHARGING_CURRENT_REGEX = """Max charging current:\s+(\d+)"""
        private const val MAX_CHARGING_VOLTAGE_REGEX = """Max charging voltage:\s+(\d+)"""
        private const val CHARGE_COUNTER_REGEX = """Charge counter:\s+(\d+)"""
        private const val STATUS_REGEX = """status:\s+(\d+)"""
        private const val HEALTH_REGEX = """health:\s+(\d+)"""
        private const val PRESENT_REGEX = """present:\s+(true|false)"""
        private const val LEVEL_REGEX = """level:\s+(\d+)"""
        private const val SCALE_REGEX = """scale:\s+(\d+)"""
        private const val VOLTAGE_REGEX = """voltage:\s+(\d+)"""
        private const val TEMPERATURE_REGEX = """temperature:\s+(\d+)"""
        private const val TECHNOLOGY_REGEX = """technology:\s+([a-zA-Z-]+)"""
        private const val CHARGING_STATE_REGEX = """Charging state:\s+(\d+)"""
        private const val CHARGING_POLICY_REGEX = """Charging policy:\s+(\d+)"""
        private const val CAPACITY_LEVEL_REGEX = """Capacity level:\s+(\d+)"""
    }

}
