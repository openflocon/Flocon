package io.github.openflocon.flocondesktop.device.pages.battery

import androidx.compose.runtime.Immutable

@Immutable
data class BatteryUiState(
    val acPowered: Boolean?,
    val usbPowered: Boolean?,
    val wirelessPowered: Boolean?,
    val dockPowered: Boolean?,
    val maxChargingCurrent: Int?,
    val maxChargingVoltage: Int?,
    val chargeCounter: Int?,
    val status: Int?,
    val health: Int?,
    val present: Boolean?,
    val level: Int?,
    val scale: Int?,
    val voltage: Int?,
    val temperature: Int?,
    val technology: String?,
    val chargingState: Int?,
    val chargingPolicy: Int?,
    val capacityLevel: Int?
)

fun previewBatteryUiState() = BatteryUiState(
    acPowered = true,
    usbPowered = false,
    wirelessPowered = true,
    dockPowered = false,
    maxChargingCurrent = 100,
    maxChargingVoltage = 100,
    chargeCounter = 100,
    status = 100,
    health = 100,
    present = true,
    level = 100,
    scale = 100,
    voltage = 100,
    temperature = 100,
    technology = "Technology",
    chargingState = 100,
    chargingPolicy = 100,
    capacityLevel = 100
)
