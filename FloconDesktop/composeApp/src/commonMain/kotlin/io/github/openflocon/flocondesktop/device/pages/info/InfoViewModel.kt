package io.github.openflocon.flocondesktop.device.pages.info

import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.flocondesktop.device.PageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InfoViewModel(
    deviceSerial: String,
    sendCommandUseCase: SendCommandUseCase
) : PageViewModel(deviceSerial, sendCommandUseCase) {

    private val _uiState = MutableStateFlow(
        InfoUiState(model = "", brand = "", versionRelease = "", versionSdk = "", serialNumber = "", battery = "")
    )
    val uiState = _uiState.asStateFlow()

    init {
        deviceInfo()
    }

    fun onAction(action: InfoAction) {

    }

    private fun deviceInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state ->
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

}
