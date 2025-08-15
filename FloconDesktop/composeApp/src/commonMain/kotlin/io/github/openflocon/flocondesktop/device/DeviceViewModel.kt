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
        sendCommand()
    }

    fun onAction(action: DeviceAction) {

    }

    private fun sendCommand() {
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
