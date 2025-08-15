package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DeviceViewModel(
    val deviceSerial: String,
    val sendCommandUseCase: SendCommandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DeviceUiState(
            selectedIndex = 0,
            model = "",
            brand = "",
            versionRelease = "",
            versionSdk = "",
            serialNumber = "",
            battery = "",
            cpu = "",
            mem = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        onRefresh()
        deviceInfo()
    }

    fun onAction(action: DeviceAction) {
        when (action) {
            is DeviceAction.SelectTab -> onSelect(action)
            DeviceAction.Refresh -> onRefresh()
        }
    }

    private fun onSelect(action: DeviceAction.SelectTab) {
        _uiState.update { it.copy(selectedIndex = action.index) }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    cpu = sendCommandUseCase("shell", "dumpsys", "cpuinfo").getOrNull().orEmpty(),
                    battery = sendCommandUseCase("shell", "dumpsys", "battery").getOrNull().orEmpty(),
                    mem = sendCommandUseCase("shell", "dumpsys", "meminfo").getOrNull().orEmpty(),
                )
            }
        }
    }

    private fun deviceInfo() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    model = sendCommandUseCase("shell", "getprop", "ro.product.model").getOrNull().orEmpty().replace("\n", ""),
                    brand = sendCommandUseCase("shell", "getprop", "ro.product.brand").getOrNull().orEmpty().replace("\n", ""),
                    versionRelease = sendCommandUseCase("shell", "getprop", "ro.build.version.release").getOrNull().orEmpty().replace("\n", ""),
                    versionSdk = sendCommandUseCase("shell", "getprop", "ro.build.version.sdk").getOrNull().orEmpty().replace("\n", ""),
                    serialNumber = sendCommandUseCase("shell", "getprop", "ro.serialno").getOrNull().orEmpty().replace("\n", ""),
                )
            }
        }
    }

}
