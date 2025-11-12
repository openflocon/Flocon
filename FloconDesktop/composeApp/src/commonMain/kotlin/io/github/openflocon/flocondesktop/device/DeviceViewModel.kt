package io.github.openflocon.flocondesktop.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import io.github.openflocon.domain.common.getOrNull
import io.github.openflocon.flocondesktop.device.models.ContentUiState
import io.github.openflocon.flocondesktop.device.models.DeviceUiState
import io.github.openflocon.flocondesktop.device.pages.memory.MemoryItem
import io.github.openflocon.flocondesktop.device.pages.memory.MemoryUiState
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
    private val deviceSerial = MutableStateFlow("")

    val uiState = combine(
        contentState,
        deviceSerial
    ) { states ->
        DeviceUiState(
            contentState = states[0] as ContentUiState,
            deviceSerial = states[1] as String
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeviceUiState(
                contentState = contentState.value,
                deviceSerial = deviceSerial.value
            )
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            deviceSerial.value = deviceSerialUseCase(deviceId)
        }
    }

    fun onAction(action: DeviceAction) {
        when (action) {
            is DeviceAction.SelectTab -> onSelect(action)
        }
    }

    private fun onSelect(action: DeviceAction.SelectTab) {
        contentState.update { it.copy(selectedTab = action.selected) }
    }

    private suspend fun sendCommand(vararg args: String): String {
        return sendCommandUseCase(deviceSerial.value, *args)
            .getOrNull()
            .orEmpty()
            .removeSuffix("\n")
    }

}
