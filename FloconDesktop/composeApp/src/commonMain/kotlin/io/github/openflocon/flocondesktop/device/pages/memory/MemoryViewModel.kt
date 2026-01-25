package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MemoryViewModel(
    deviceSerial: String,
    sendCommandUseCase: SendCommandUseCase
) : PageViewModel(deviceSerial, sendCommandUseCase) {

    private val _uiState = MutableStateFlow(MemoryUiState(emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                refreshMemory()
                delay(10.seconds)
            }
        }
    }

    fun onAction(action: MemoryAction) {

    }

    private suspend fun refreshMemory() {
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

        _uiState.update { it.copy(list = items) }
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
        private const val MEM_REGEX = """([\d,]+)K:\s+([a-zA-Z0-9._:-]+)\s+$${"pid"}\s+(\d+)(?:\s+/\s+([a-zA-Z\s]+))?$"""
    }

}
