package io.github.openflocon.flocondesktop.device.pages.cpu

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CpuViewModel(
    deviceSerial: String,
    sendCommandUseCase: SendCommandUseCase
) : PageViewModel(deviceSerial, sendCommandUseCase) {

    private val _uiState = MutableStateFlow(CpuUiState(emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        refreshCpu()
    }

    fun onAction(action: CpuAction) {

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

            _uiState.update { it.copy(list = items) }
        }
    }

    companion object {
        // CPU
        private const val CPU_REGEX =
            """(\d+(?:\.\d+)?)%\s+([^:]+):\s+(\d+(?:\.\d+)?)%\s+user\s+\+\s+(\d+(?:\.\d+)?)%\s+kernel\s+/ faults:\s+(\d+)\s+minor\s+(\d+)\s+major"""
    }

}
