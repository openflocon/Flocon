package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.device.models.BatteryUiState
import io.github.openflocon.flocondesktop.device.models.ContentUiState
import io.github.openflocon.flocondesktop.device.models.CpuItem
import io.github.openflocon.flocondesktop.device.models.CpuUiState
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
import io.github.openflocon.flocondesktop.device.models.InfoUiState
import io.github.openflocon.flocondesktop.device.models.MemoryItem
import io.github.openflocon.flocondesktop.device.models.MemoryUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DeviceViewModel(
    val deviceId: String,
    val sendCommandUseCase: SendCommandUseCase,
    val deviceSerialUseCase: GetDeviceSerialUseCase,
    val currentDeviceAppsUseCase: GetCurrentDeviceIdAndPackageNameUseCase
) : ViewModel() {

    private val contentState = MutableStateFlow(ContentUiState(selectedTab = DeviceTab.entries.first()))
    private val infoState = MutableStateFlow(
        InfoUiState(
            model = "",
            brand = "",
            versionRelease = "",
            versionSdk = "",
            serialNumber = "",
            battery = ""
        )
    )
    private val batteryState = MutableStateFlow(
        BatteryUiState(
            acPowered = false,
            usbPowered = false,
            wirelessPowered = false,
            dockPowered = false,
            maxChargingCurrent = 0,
            maxChargingVoltage = 0,
            chargeCounter = 0,
            status = 0,
            health = 0,
            present = false,
            level = 0,
            scale = 0,
            voltage = 0,
            temperature = 0,
            technology = "",
            chargingState = 0,
            chargingPolicy = 0,
            capacityLevel = 0
        )
    )
    private val memoryState = MutableStateFlow(MemoryUiState(emptyList()))
    private val cpuState = MutableStateFlow(CpuUiState(emptyList()))
    private val deviceSerial = MutableStateFlow("")

    val uiState = combine(
        contentState,
        infoState,
        memoryState,
        cpuState,
        batteryState,
        deviceSerial
    ) { states ->
        DeviceUiState(
            contentState = states[0] as ContentUiState,
            infoState = states[1] as InfoUiState,
            memoryState = states[2] as MemoryUiState,
            cpuState = states[3] as CpuUiState,
            batteryState = states[4] as BatteryUiState,
            deviceSerial = states[5] as String
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeviceUiState(
                contentState = contentState.value,
                infoState = infoState.value,
                memoryState = memoryState.value,
                cpuState = cpuState.value,
                batteryState = batteryState.value,
                deviceSerial = deviceSerial.value
            )
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            deviceSerial.value = deviceSerialUseCase(deviceId)

            onRefresh()
        }
    }

    fun onAction(action: DeviceAction) {
        when (action) {
            is DeviceAction.SelectTab -> onSelect(action)
            DeviceAction.Refresh -> onRefresh()
        }
    }

    private fun onSelect(action: DeviceAction.SelectTab) {
        contentState.update { it.copy(selectedTab = action.selected) }
    }

    private fun onRefresh() {
        refreshCpu()
        refreshMemory()
        refreshBattery()
        deviceInfo()
        viewModelScope.launch {
            //                    battery = sendCommand("shell", "dumpsys", "battery"),
//                    mem = sendCommand("shell", "dumpsys", "meminfo")
        }
    }

    private fun deviceInfo() {
        viewModelScope.launch {
            infoState.update { state ->
                state.copy(
                    model = sendCommand("shell", "getprop", "ro.product.model"),
                    brand = sendCommand("shell", "getprop", "ro.product.brand"),
                    versionRelease = sendCommand("shell", "getprop", "ro.build.version.release"),
                    versionSdk = sendCommand("shell", "getprop", "ro.build.version.sdk"),
                    serialNumber = sendCommand("shell", "getprop", "ro.serialno")
                )
            }
        }
    }

    private suspend fun sendCommand(vararg args: String): String {
        return sendCommandUseCase(deviceSerial.value, *args)
            .getOrNull()
            .orEmpty()
            .removeSuffix("\n")
    }

    private fun refreshCpu() {
        viewModelScope.launch(Dispatchers.IO) {
            val output = sendCommand("shell", "dumpsys", "cpuinfo")
            val regex = CPU_REGEX.toRegex()
            val items = output.lineSequence()
                .mapNotNull { regex.find(it) }
                .mapNotNull {
                    try {
                        val packageName = it.groupValues[2].split("/")

                        CpuItem(
                            cpuUsage = it.groupValues[1].toDoubleOrNull() ?: return@mapNotNull null,
                            packageName = packageName[1],
                            pId = packageName[0].toIntOrNull() ?: return@mapNotNull null,
                            userPercentage = it.groupValues[3].toDoubleOrNull() ?: return@mapNotNull null,
                            kernelPercentage = it.groupValues[4].toDoubleOrNull() ?: return@mapNotNull null,
                            minorFaults = it.groupValues[5].toIntOrNull(),
                            majorFaults = it.groupValues[6].toIntOrNull()
                        )
                    } catch (e: NumberFormatException) {
                        // Handle parsing errors gracefully (e.g., log the error)
                        null
                    }
                }
                .sortedByDescending(CpuItem::cpuUsage)
                .distinctBy(CpuItem::packageName)
                .toList()

            cpuState.update { it.copy(list = items) }
        }
    }

    private fun refreshMemory() {
        viewModelScope.launch(Dispatchers.IO) {
            val output = sendCommand("shell", "dumpsys", "meminfo")
            val regex = MEM_REGEX.toRegex()
            val items = output.lineSequence()
                .map { it.trim() }
                .mapNotNull { regex.find(it) }
                .mapNotNull {
                    try {
                        MemoryItem(
                            memoryUsage = formatMemoryUsage(
                                memoryUsageKB = it.groupValues[1].replace(",", "").toDoubleOrNull()
                            ),
                            processName = it.groupValues[2],
                            pid = it.groupValues[3].toIntOrNull() ?: return@mapNotNull null
                        )
                    } catch (e: NumberFormatException) {
                        // Handle parsing errors gracefully (e.g., log the error)
                        null
                    }
                }
                .toList()

            memoryState.update { it.copy(list = items) }
        }
    }

    private fun formatMemoryUsage(memoryUsageKB: Double?): String {
        if (memoryUsageKB == null) {
            return "N/A" // Or handle null case as appropriate
        }

        val memoryUsage = memoryUsageKB * 1024 // Convert KB to bytes

        return when {
            memoryUsage < 1024 -> String.format("%.2f B", memoryUsage)
            memoryUsage < 1024 * 1024 -> String.format("%.2f KB", memoryUsage / 1024)
            memoryUsage < 1024 * 1024 * 1024 -> String.format("%.2f MB", memoryUsage / (1024 * 1024))
            else -> String.format("%.2f GB", memoryUsage / (1024 * 1024 * 1024))
        }
    }

    private fun refreshBattery() {
        viewModelScope.launch {
            val batteryInfo = sendCommand("shell", "dumpsys", "battery")

            batteryState.update {
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

        // CPU
        private const val CPU_REGEX =
            """(\d+(?:\.\d+)?)%\s+([^:]+):\s+(\d+(?:\.\d+)?)%\s+user\s+\+\s+(\d+(?:\.\d+)?)%\s+kernel\s+/ faults:\s+(\d+)\s+minor\s+(\d+)\s+major"""

        // MEM
        private const val MEM_REGEX = """([\d,]+)K:\s+([a-zA-Z0-9._:-]+)\s+$${"pid"}\s+(\d+)(?:\s+/\s+([a-zA-Z\s]+))?$"""

        // Battery
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
