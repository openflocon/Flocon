package io.github.openflocon.flocondesktop.device.pages.memory

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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

    private val _uiState = MutableStateFlow(MemoryUiState(persistentListOf()))
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
        val items = output.lineSequence()
            .map { it.trim() }
            .mapNotNull { MEM_REGEX.find(it) }
            .mapNotNull {
                val memoryUsageKB = it.groupValues[1].replace(",", "").toDoubleOrNull()
                val pid = it.groupValues[3].toIntOrNull() ?: return@mapNotNull null
                Triple(memoryUsageKB ?: 0.0, it.groupValues[2], pid)
            }
            // a process is listed in several sections of the dump, and the pid is the list key
            .distinctBy { (_, _, pid) -> pid }
            .sortedByDescending { (memoryUsageKB, _, _) -> memoryUsageKB }
            .map { (memoryUsageKB, processName, pid) ->
                MemoryItem(
                    memoryUsage = formatMemoryUsage(memoryUsageKB),
                    processName = processName,
                    pid = pid
                )
            }
            .toImmutableList()

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
        // e.g. "157,332K: com.example.app (pid 1234 / activities)"
        private val MEM_REGEX = """([\d,]+)K:\s+(\S+)\s+\(pid\s+(\d+)[^)]*\)""".toRegex()
    }
}
