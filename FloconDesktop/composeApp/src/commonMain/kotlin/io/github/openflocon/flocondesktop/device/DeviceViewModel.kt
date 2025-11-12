package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.flocondesktop.device.models.ContentUiState
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
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
    val deviceSerialUseCase: GetDeviceSerialUseCase
) : ViewModel() {

    private val contentState = MutableStateFlow(ContentUiState(selectedTab = DeviceTab.entries.first()))
    private val memoryState = MutableStateFlow(MemoryUiState(emptyList()))
    private val deviceSerial = MutableStateFlow("")

    val uiState = combine(
        contentState,
        memoryState,
        deviceSerial
    ) { states ->
        DeviceUiState(
            contentState = states[0] as ContentUiState,
            memoryState = states[1] as MemoryUiState,
            deviceSerial = states[2] as String
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeviceUiState(
                contentState = contentState.value,
                memoryState = memoryState.value,
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
        refreshMemory()
    }

    private suspend fun sendCommand(vararg args: String): String {
        return sendCommandUseCase(deviceSerial.value, *args)
            .getOrNull()
            .orEmpty()
            .removeSuffix("\n")
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

    companion object {

        // MEM
        private const val MEM_REGEX = """([\d,]+)K:\s+([a-zA-Z0-9._:-]+)\s+$${"pid"}\s+(\d+)(?:\s+/\s+([a-zA-Z\s]+))?$"""
    }

}
